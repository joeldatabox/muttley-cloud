package br.com.muttley.security.infra.feign;

import br.com.muttley.model.Historic;
import br.com.muttley.rest.hateoas.resource.PageableResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface RestControllerClient<T extends Serializable> {

    /**
     * Save only a new record
     *
     * @param value -> object to salve
     */
    @RequestMapping(method = POST, consumes = {APPLICATION_JSON_UTF8_VALUE})
    void save(@RequestBody T value);

    /**
     * Save a new record and return a persistent item
     *
     * @param value -> object to salve
     * @return object salved
     */
    @RequestMapping(method = POST, consumes = {APPLICATION_JSON_UTF8_VALUE}, params = {"returnEntity=true"})
    T merger(@RequestBody T value);

    /**
     * Update a record
     *
     * @param id    -> id of record
     * @param model -> object to update
     * @return model -> record updated
     */
    @RequestMapping(value = "/{id}", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    T update(@PathVariable("id") String id, @RequestBody T model);

    /**
     * Remove a simple record whit id
     *
     * @param id -> id of record
     */
    @RequestMapping(value = "/{id}", method = DELETE)
    void deleteById(@PathVariable("id") String id);

    /**
     * Find a record by id
     *
     * @param id -> id of record
     * @return record found
     */
    @RequestMapping(value = "/{id}", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    T findById(@PathVariable("id") String id);

    /**
     * Return first record found in database
     */
    @RequestMapping(value = "/first", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    T first();

    /**
     * @param id -> id of record
     * @return change history
     */
    @RequestMapping(value = "/{id}/historic", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    Historic loadHistoric(@PathVariable("id") String id);

    /**
     * @param allRequestParams -> all parameters in request
     * @return A pageable list of records
     */
    @RequestMapping(method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    PageableResource<T> list(@RequestParam Map<String, String> allRequestParams);

    /**
     * @return A pageable list of records
     */
    @RequestMapping(method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    PageableResource<T> list();

    /**
     * Count total record
     *
     * @param allRequestParams -> all parameters in request
     * @return total record
     */
    @RequestMapping(value = "/count", method = GET, consumes = TEXT_PLAIN_VALUE)
    Long count(@RequestParam Map<String, String> allRequestParams);

    /**
     * Count total record
     *
     * @return total record
     */
    @RequestMapping(value = "/count", method = GET, consumes = TEXT_PLAIN_VALUE)
    Long count();
}
