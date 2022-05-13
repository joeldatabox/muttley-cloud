package br.com.muttley.rest;

import br.com.muttley.rest.hateoas.resource.PageableResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

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
    @RequestMapping(method = POST, consumes = {APPLICATION_JSON_UTF8_VALUE})
    T save(@RequestBody T value, @RequestParam(required = false, value = "returnEntity", defaultValue = "") String returnEntity);

    @RequestMapping(value = "/{id}", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    T update(@PathVariable("id") String id, @RequestBody T model);

    @RequestMapping(value = "/{id}", method = DELETE)
    void deleteById(@PathVariable("id") String id);

    @RequestMapping(value = "/{id}", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    T findById(@PathVariable("id") String id);

    @RequestMapping(value = "/reference/{id}", method = GET, produces = APPLICATION_JSON_UTF8_VALUE)
    T findReferenceById(@PathVariable("id") String id);

    @RequestMapping(value = "/ids", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    Set<T> findByIds(@RequestParam(required = false, value = "ids") String[] ids);

    @RequestMapping(value = "/first", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    T first();

    @RequestMapping(method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    PageableResource list(@RequestParam Map<String, String> allRequestParams);

    @RequestMapping(method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    PageableResource list();

    @RequestMapping(value = "/count", method = GET, consumes = TEXT_PLAIN_VALUE)
    Long count(@RequestParam Map<String, String> allRequestParams);

    @RequestMapping(value = "/count", method = GET, consumes = TEXT_PLAIN_VALUE)
    Long count();
}
