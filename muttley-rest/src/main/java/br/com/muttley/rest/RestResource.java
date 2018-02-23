package br.com.muttley.rest;

import br.com.muttley.domain.service.Service;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyPageableRequestException;
import br.com.muttley.model.Model;
import br.com.muttley.model.security.model.User;
import br.com.muttley.mongo.service.infra.Operators;
import br.com.muttley.rest.hateoas.event.PaginatedResultsRetrievedEvent;
import br.com.muttley.rest.hateoas.event.ResourceCreatedEvent;
import br.com.muttley.rest.hateoas.event.SingleResourceRetrievedEvent;
import br.com.muttley.rest.hateoas.resource.MetadataPageable;
import br.com.muttley.rest.hateoas.resource.PageableResource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Joel Rodrigues Moreira on 23/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface RestResource {
    /**
     * Dispara um evento toda vez que um recurso é criado
     *
     * @param eventPublisher -> publicador de envento
     * @param response       -> objeto response
     * @param model          -> objeto criado
     */
    default void publishCreateResourceEvent(final ApplicationEventPublisher eventPublisher, final HttpServletResponse response, final Model model) {
        eventPublisher.publishEvent(new ResourceCreatedEvent(this, response, model));
    }

    /**
     * Dispara um evento toda vez que um recurso unico é encontrado
     *
     * @param eventPublisher -> publicador de evento
     * @param response       -> objeto response
     */
    default void publishSingleResourceRetrievedEvent(final ApplicationEventPublisher eventPublisher, final HttpServletResponse response) {
        eventPublisher.publishEvent(new SingleResourceRetrievedEvent(this, response));
    }

    /**
     * Dispara um evento toda vez em que se listar um registro
     *
     * @param eventPublisher       -> publicador de evento
     * @param response             -> objeto response
     * @param uriComponentsBuilder ->
     * @param metadataPageable     -> metadatas de paginação
     */
    default void publishPaginatedResultsRetrievedEvent(final ApplicationEventPublisher eventPublisher, final HttpServletResponse response, final UriComponentsBuilder uriComponentsBuilder, final MetadataPageable metadataPageable) {
        eventPublisher.publishEvent(
                new PaginatedResultsRetrievedEvent<>(this, uriComponentsBuilder, response, metadataPageable)
        );
    }

    /**
     * Realiza a paginação de registro utilizando o padrão Rest
     *
     * @param service -> serviço com regra de negócio
     * @param user    -> user atual da requisição
     * @param params  -> parametros da requisição
     */
    default PageableResource toPageableResource(final ApplicationEventPublisher eventPublisher, final HttpServletResponse response, final Service service, final User user, final Map<String, String> params) {
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

        final Long recordSize = Long.valueOf(records.size());

        final MetadataPageable metadataPageable = new MetadataPageable(
                ServletUriComponentsBuilder.fromCurrentRequest(),
                LIMIT,
                SKIP,
                recordSize, total);

        publishPaginatedResultsRetrievedEvent(
                eventPublisher,
                response,
                ServletUriComponentsBuilder.fromCurrentRequest(),
                metadataPageable
        );


        return new PageableResource(records, metadataPageable);
    }

    /**
     * Remove parametros denecessários para contagem (limit, page)
     *
     * @param allRequestParams -> parametros da requisição
     */
    default Map<String, Object> createQueryParamForCount(final Map<String, Object> allRequestParams) {
        return allRequestParams
                .entrySet()
                .stream()
                .filter(key ->
                        !key.getKey().equals(Operators.LIMIT.toString()) && !key.getKey().equals(Operators.SKIP.toString())
                )
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    /**
     * Valida parametros passado na requisição para paginação
     *
     * @param allRequestParams -> parametro da requisição
     */
    default Map<String, Object> validPageable(final Map<String, String> allRequestParams) {
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
}
