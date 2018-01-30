package br.com.muttley.rest;

import br.com.muttley.domain.service.Service;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyPageableRequestException;
import br.com.muttley.exception.throwables.security.MuttleySecurityCredentialException;
import br.com.muttley.model.Model;
import br.com.muttley.model.security.model.User;
import br.com.muttley.model.security.model.enumeration.Authorities;
import br.com.muttley.mongo.service.infra.Operators;
import br.com.muttley.rest.hateoas.event.PaginatedResultsRetrievedEvent;
import br.com.muttley.rest.hateoas.event.ResourceCreatedEvent;
import br.com.muttley.rest.hateoas.event.SingleResourceRetrievedEvent;
import br.com.muttley.rest.hateoas.resource.MetadataPageable;
import br.com.muttley.rest.hateoas.resource.PageableResource;
import br.com.muttley.security.infra.service.UserService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public abstract class DefaultRestController<T extends Model, ID extends Serializable> {
    private final Service<T, ID> service;
    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    public DefaultRestController(final Service service, final UserService userService, final ApplicationEventPublisher eventPublisher) {
        this.service = service;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(@Valid @RequestBody final T value, final HttpServletResponse response, @RequestParam(required = false, value = "returnEntity", defaultValue = "") final String returnEntity) {
        this.checkCreate();
        final T record = service.save(this.userService.getCurrentUser(), value);
        publishCreateResourceEvent(response, record);
        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity update(@PathVariable("id") final String id, @RequestBody final T model) {
        checkUpdate();
        model.setId(id);
        return ResponseEntity.ok(service.update(this.userService.getCurrentUser(), model));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity deleteById(@PathVariable("id") final ID id) {
        checkDelete();
        service.deleteById(this.userService.getCurrentUser(), id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity findById(@PathVariable("id") final ID id, final HttpServletResponse response) {
        checkRead();
        this.publishSingleResourceRetrievedEvent(response);
        return ResponseEntity.ok(service.findById(this.userService.getCurrentUser(), id));
    }

    @RequestMapping(value = "/first", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity first(final HttpServletResponse response) {
        checkRead();
        final T value = service.findFirst(this.userService.getCurrentUser());
        this.publishSingleResourceRetrievedEvent(response);
        return ResponseEntity.ok(service.findFirst(this.userService.getCurrentUser()));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<PageableResource> list(@RequestParam final Map<String, String> allRequestParams) {
        checkRead();
        return ResponseEntity.ok(toPageableResource(this.userService.getCurrentUser(), allRequestParams));
    }

    @RequestMapping(value = "/count", method = RequestMethod.GET, produces = {MediaType.TEXT_PLAIN_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public final ResponseEntity<String> count(final Map<String, Object> allRequestParams) {
        checkRead();
        return ResponseEntity.ok(String.valueOf(service.count(this.userService.getCurrentUser(), allRequestParams)));
    }

    /**
     * Dispara um evento toda vez que um recurso é criado
     */
    protected final void publishCreateResourceEvent(final HttpServletResponse response, final Model model) {
        this.eventPublisher.publishEvent(new ResourceCreatedEvent(this, response, model));
    }

    /**
     * Dispara um evento toda vez que um recurso unico é encontrado
     */
    protected final void publishSingleResourceRetrievedEvent(final HttpServletResponse response) {
        this.eventPublisher.publishEvent(new SingleResourceRetrievedEvent(this, response));
    }

    /**
     * Dispara um evento toda vez em que se listar um registro
     **/
    protected final void publishPaginatedResultsRetrievedEvent(final HttpServletResponse response, final UriComponentsBuilder uriComponentsBuilder, final Long page, final Long pageSize, final Long totalpages, final Long totalRecords) {
        this.eventPublisher.publishEvent(
                new PaginatedResultsRetrievedEvent<>(this, uriComponentsBuilder, response, page, pageSize, totalpages, totalRecords)
        );
    }

    protected final PageableResource toPageableResource(final User user, final Map<String, String> params) {
        //validando os parametros passados
        final Map<String, Object> allRequestParams = validPageable(params);
        final Long SKIP = Long.valueOf(allRequestParams.get(Operators.SKIP.toString()).toString());
        final Long LIMIT = Long.valueOf(allRequestParams.get(Operators.LIMIT.toString()).toString());

        final long total = service.count(user, createQueryParamForCount(allRequestParams));

        if (total == 0) {
            throw new MuttleyNoContentException(null, null, "registros não encontrados!");
        }

        final List records = service
                .findAll(user, allRequestParams);

        return new PageableResource(
                records,
                new MetadataPageable(
                        ServletUriComponentsBuilder.fromCurrentRequest(),
                        LIMIT,
                        SKIP,
                        Long.valueOf(records.size()), total));
    }

    /**
     * Valida parametros passado na requisição para paginação
     */
    private final Map<String, Object> validPageable(final Map<String, String> allRequestParams) {
        final MuttleyPageableRequestException ex = new MuttleyPageableRequestException();

        if (allRequestParams.containsKey(Operators.LIMIT.toString())) {
            Integer limit = null;
            try {
                limit = Integer.valueOf(allRequestParams.get(Operators.LIMIT.toString()));
                if (limit > 100) {
                    ex.addDetails(Operators.LIMIT.toString(), "o limite informado foi (" + limit + ") mas o maxímo é(100)");
                }
            } catch (NumberFormatException nex) {
                ex.addDetails(Operators.LIMIT.toString(), "deve conter um numero com o tamanho maximo de 100");
            }
        } else {
            allRequestParams.put(Operators.LIMIT.toString(), "100");
        }

        if (allRequestParams.containsKey(Operators.SKIP.toString())) {
            Integer page = null;
            try {
                page = Integer.valueOf(allRequestParams.get(Operators.SKIP.toString()));
                if (page < 0) {
                    ex.addDetails(Operators.SKIP.toString(), "a pagina informada foi (" + page + ") mas a deve ter o tamanho minimo de (0)");
                }
            } catch (final NumberFormatException nex) {
                ex.addDetails(Operators.SKIP.toString(), "deve conter um numero com o tamanho minimo de 0");
            }
        } else {
            allRequestParams.put(Operators.SKIP.toString(), "0");
        }

        if (ex.containsDetais()) {
            throw ex;
        }
        return new HashMap<>(allRequestParams);
    }

    /**
     * Remove parametros denecessarios para contagem (limit, page)
     */
    private Map<String, Object> createQueryParamForCount(final Map<String, Object> allRequestParams) {
        return allRequestParams
                .entrySet()
                .stream()
                .filter(key ->
                        !key.getKey().equals(Operators.LIMIT.toString()) && !key.getKey().equals(Operators.SKIP.toString())
                )
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    protected String[] getCreateRoles() {
        return null;
    }

    protected String[] getReadRoles() {
        return null;
    }

    protected String[] getUpdateRoles() {
        return null;
    }

    protected String[] getDeleteRoles() {
        return null;
    }

    protected final void checkCreate() {
        if (!isNull(getCreateRoles()))
            this.checkCredentials(getCreateRoles());

    }

    protected final void checkRead() {
        if (!isNull(getReadRoles()))
            this.checkCredentials(getReadRoles());
    }

    protected final void checkUpdate() {
        if (!isNull(getUpdateRoles()))
            this.checkCredentials(this.getUpdateRoles());
    }

    protected final void checkDelete() {
        if (!isNull(getDeleteRoles()))
            this.checkCredentials(this.getDeleteRoles());
    }

    protected final void checkCredentials(final String... roles) {
        if (!this.userService.getCurrentUser().inAnyRole(roles)) {
            throw new MuttleySecurityCredentialException("Você não tem permissão para acessar este recurso ")
                    .addDetails("isNecessary", roles);
        }
    }

    protected final void checkCredentials(final Authorities... roles) {
        if (!this.userService.getCurrentUser().inAnyRole(roles)) {
            throw new MuttleySecurityCredentialException("Você não tem permissão para acessar este recurso ")
                    .addDetails("isNecessary", roles);
        }
    }
}
