package br.com.muttley.rest;

import br.com.muttley.rest.hateoas.resource.PageableResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

/**
 * @author Joel Rodrigues Moreira on 23/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface RestController<T> {
    @RequestMapping(method = RequestMethod.POST, consumes = {APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    ResponseEntity save(@RequestBody T value, HttpServletResponse response, @RequestParam(required = false, value = "returnEntity", defaultValue = "") String returnEntity);

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity update(@PathVariable("id") String id, @RequestBody T model);

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = {APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity deleteById(@PathVariable("id") String id);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity findById(@PathVariable("id") String id, HttpServletResponse response);

    @RequestMapping(value = "/first", method = RequestMethod.GET, consumes = {APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity first(HttpServletResponse response);

    @RequestMapping(value = "/{id}/historic", method = RequestMethod.GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity loadHistoric(@PathVariable("id") String id, HttpServletResponse response);

    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<PageableResource<T>> list(HttpServletResponse response, @RequestParam Map<String, String> allRequestParams);

    @RequestMapping(value = "/count", method = RequestMethod.GET, consumes = TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Long> count(Map<String, Object> allRequestParams);
}
