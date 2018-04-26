package br.com.muttley.rest;

import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.rest.hateoas.resource.PageableResource;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 25/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractProxyRestController<T extends Document, ID extends Serializable> implements RestController<T, ID>, RestResource {

    private final RestControllerClient<T> client;
    private final ApplicationEventPublisher eventPublisher;

    public AbstractProxyRestController(final RestControllerClient client, final ApplicationEventPublisher eventPublisher) {
        this.client = client;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public ResponseEntity save(final T value, final HttpServletResponse response, final String returnEntity) {
        final T record = client.save(value, returnEntity);

        publishCreateResourceEvent(this.eventPublisher, response, record);

        if (returnEntity != null && returnEntity.equals("true")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(record);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @Override
    public ResponseEntity update(final String id, final T model) {
        return ResponseEntity.ok(client.update(id, model));
    }

    @Override
    public ResponseEntity deleteById(final String id) {
        client.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity findById(final String id, final HttpServletResponse response) {
        final T value = client.findById(id);

        publishSingleResourceRetrievedEvent(this.eventPublisher, response);

        return ResponseEntity.ok(value);
    }

    @Override
    public ResponseEntity first(final HttpServletResponse response) {
        final T value = client.first();
        publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(value);
    }

    @Override
    public ResponseEntity loadHistoric(final String id, final HttpServletResponse response) {
        final Historic historic = client.loadHistoric(id);
        publishSingleResourceRetrievedEvent(this.eventPublisher, response);
        return ResponseEntity.ok(historic);
    }

    @Override
    public ResponseEntity<PageableResource> list(final HttpServletResponse response, final Map<String, String> allRequestParams) {
        final PageableResource pageableResource = client.list(allRequestParams);
        return ResponseEntity.ok(toPageableResource(eventPublisher, response, pageableResource));
    }

    @Override
    public ResponseEntity<String> count(final Map<String, Object> allRequestParams) {
        return ResponseEntity.ok(String.valueOf(client.count(allRequestParams)));
    }
}
