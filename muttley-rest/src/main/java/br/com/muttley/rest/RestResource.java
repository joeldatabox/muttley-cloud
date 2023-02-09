package br.com.muttley.rest;

import br.com.muttley.domain.service.Service;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyPageableRequestException;
import br.com.muttley.model.Document;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.service.infra.Operator;
import br.com.muttley.rest.hateoas.event.PaginatedResultsRetrievedEvent;
import br.com.muttley.rest.hateoas.event.ResourceCreatedEvent;
import br.com.muttley.rest.hateoas.event.SingleResourceRetrievedEvent;
import br.com.muttley.rest.hateoas.resource.MetadataPageable;
import br.com.muttley.rest.hateoas.resource.PageableResource;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Joel Rodrigues Moreira on 23/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface RestResource<T extends Document> {
    /**
     * Dispara um evento toda vez que um recurso é criado
     *
     * @param eventPublisher -> publicador de envento
     * @param response       -> objeto response
     * @param model          -> objeto criado
     */
    default void publishCreateResourceEvent(final ApplicationEventPublisher eventPublisher, final HttpServletResponse response, final Document model) {
        eventPublisher.publishEvent(this.newResourceCreatedEvent(model, response));
    }

    /**
     * Cria evento a ser disparado toda vez que algum recurso é criado
     *
     * @param model    -> objeto criado
     * @param response -> objeto response
     */
    default ApplicationEvent newResourceCreatedEvent(final Document model, final HttpServletResponse response) {
        return new ResourceCreatedEvent(model, response);
    }

    /**
     * Dispara um evento toda vez que um recurso unico é encontrado
     *
     * @param eventPublisher -> publicador de evento
     * @param response       -> objeto response
     */
    default void publishSingleResourceRetrievedEvent(final ApplicationEventPublisher eventPublisher, final HttpServletResponse response) {
        eventPublisher.publishEvent(this.newSingleResourceRetrievedEvent(response));
    }

    /**
     * Cria um evento toda vez que um recurso unico é encontrado
     *
     * @param response -> objeto response
     */
    default ApplicationEvent newSingleResourceRetrievedEvent(final HttpServletResponse response) {
        return new SingleResourceRetrievedEvent(response);
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
        final Map<String, String> allRequestParams = validPageable(params);
        final Long SKIP = Long.valueOf(allRequestParams.get(Operator.SKIP.toString()).toString());
        final Long LIMIT = Long.valueOf(allRequestParams.get(Operator.LIMIT.toString()).toString());

        final long total = service.count(user, createQueryParamForCount(allRequestParams));

        if (total == 0) {
            throw new MuttleyNoContentException(null, null, "registros não encontrados!");
        }
        final long totalAll = service.count(user, null);

        final List records = service
                .findAll(user, allRequestParams);

        final Long recordSize = Long.valueOf(records.size());

        final MetadataPageable metadataPageable = new MetadataPageable(
                ServletUriComponentsBuilder.fromCurrentRequest(),
                LIMIT,
                SKIP,
                recordSize,
                total,
                totalAll);

        publishPaginatedResultsRetrievedEvent(
                eventPublisher,
                response,
                ServletUriComponentsBuilder.fromCurrentRequest(),
                metadataPageable
        );


        return new PageableResource(records, metadataPageable);
    }

    default PageableResource toPageableResource(final ApplicationEventPublisher eventPublisher, final HttpServletResponse response, final List records, final Long total, final Long skip, final Long limit) {
        if (total == 0) {
            throw new MuttleyNoContentException(null, null, "registros não encontrados!");
        }

        final Long recordSize = Long.valueOf(records.size());

        final MetadataPageable metadataPageable = new MetadataPageable(
                ServletUriComponentsBuilder.fromCurrentRequest(),
                limit,
                skip,
                recordSize, total);

        publishPaginatedResultsRetrievedEvent(
                eventPublisher,
                response,
                ServletUriComponentsBuilder.fromCurrentRequest(),
                metadataPageable
        );

        return new PageableResource(records, metadataPageable);
    }

    default PageableResource toPageableResource(final ApplicationEventPublisher eventPublisher, final HttpServletResponse response, final List records, final Long total, final Long totalAll, final Long skip, final Long limit) {
        if (total == 0) {
            throw new MuttleyNoContentException(null, null, "registros não encontrados!");
        }

        final Long recordSize = Long.valueOf(records.size());

        final MetadataPageable metadataPageable = new MetadataPageable(
                ServletUriComponentsBuilder.fromCurrentRequest(),
                limit,
                skip,
                recordSize,
                total,
                totalAll);

        publishPaginatedResultsRetrievedEvent(
                eventPublisher,
                response,
                ServletUriComponentsBuilder.fromCurrentRequest(),
                metadataPageable
        );

        return new PageableResource(records, metadataPageable);
    }

    /**
     * Realiza a paginação de registro utilizando o padrão Rest
     */
    default PageableResource toPageableResource(final ApplicationEventPublisher eventPublisher, final HttpServletResponse response, final PageableResource pageableResource) {

        if (pageableResource.isEmpty()) {
            throw new MuttleyNoContentException(null, null, "registros não encontrados!");
        }

        publishPaginatedResultsRetrievedEvent(
                eventPublisher,
                response,
                ServletUriComponentsBuilder.fromCurrentRequest(),
                pageableResource.get_metadata()
        );


        return pageableResource;
    }

    /**
     * Remove parametros denecessários para contagem (limit, page)
     *
     * @param allRequestParams -> parametros da requisição
     */
    default Map<String, String> createQueryParamForCount(final Map<String, String> allRequestParams) {
        return allRequestParams
                .entrySet()
                .parallelStream()
                .filter(key ->
                        !key.getKey().equals(Operator.LIMIT.toString()) && !key.getKey().equals(Operator.SKIP.toString())
                )
                .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));
    }

    /**
     * Valida parametros passado na requisição para paginação
     *
     * @param allRequestParams -> parametro da requisição
     */
    default Map<String, String> validPageable(final Map<String, String> allRequestParams) {
        final MuttleyPageableRequestException ex = new MuttleyPageableRequestException();
        Map<String, String> map = allRequestParams == null ? new LinkedHashMap<>() : new LinkedHashMap<>(allRequestParams);
        if (map.containsKey(Operator.LIMIT.toString())) {
            Integer limit = null;
            try {
                limit = Integer.valueOf(map.get(Operator.LIMIT.toString()));
                if (limit > this.getMaxrecords()) {
                    ex.addDetails(Operator.LIMIT.toString(), "o limite informado foi (" + limit + ") mas o maxímo é(" + this.getMaxrecords() + ")");
                }
            } catch (NumberFormatException nex) {
                ex.addDetails(Operator.LIMIT.toString(), "deve conter um numero com o tamanho maximo de " + this.getMaxrecords());
            }
        } else {
            map.put(Operator.LIMIT.toString(), String.valueOf(this.getMaxrecords()));
        }

        if (map.containsKey(Operator.SKIP.toString())) {
            Integer page = null;
            try {
                page = Integer.valueOf(map.get(Operator.SKIP.toString()));
                if (page < 0) {
                    ex.addDetails(Operator.SKIP.toString(), "a pagina informada foi (" + page + ") mas a deve ter o tamanho minimo de (0)");
                }
            } catch (final NumberFormatException nex) {
                ex.addDetails(Operator.SKIP.toString(), "deve conter um numero com o tamanho minimo de 0");
            }
        } else {
            map.put(Operator.SKIP.toString(), "0");
        }

        if (ex.containsDetais()) {
            throw ex;
        }
        return map;
    }

    default int getMaxrecords() {\
        return 100;
    }
}
