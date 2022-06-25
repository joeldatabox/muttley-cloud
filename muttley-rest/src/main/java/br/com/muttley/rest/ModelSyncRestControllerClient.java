package br.com.muttley.rest;

import br.com.muttley.model.SyncObjectId;
import br.com.muttley.security.infra.resource.PageableResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Joel Rodrigues Moreira on 11/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface ModelSyncRestControllerClient<T> {

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    T save(@RequestBody T value, @RequestParam(required = false, value = "returnEntity", defaultValue = "") String returnEntity);

    @RequestMapping(value = "/{id}", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    T update(@PathVariable("id") String id, @RequestBody T model);

    @RequestMapping(value = "/sync", method = RequestMethod.PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    T updateBySync(@RequestParam(value = "sync", required = false) String sync, @RequestBody T model);

    @RequestMapping(value = "/synchronization", method = RequestMethod.PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    void synchronization(@RequestBody final List<T> values);

    @RequestMapping(value = "/{id}", method = DELETE)
    void deleteById(@PathVariable("id") String id);

    @RequestMapping(value = "/sync", method = RequestMethod.DELETE, consumes = APPLICATION_JSON_UTF8_VALUE)
    void delteBySync(@RequestParam(value = "sync", required = false) String sync);

    @RequestMapping(value = "/{id}", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    T findById(@PathVariable("id") String id);

    @RequestMapping(value = "/reference/{id}", method = GET, produces = APPLICATION_JSON_UTF8_VALUE)
    T findReferenceById(@PathVariable("id") String id);

    @RequestMapping(value = "/sync", method = RequestMethod.GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    T findBySync(@RequestParam(value = "sync", required = false) final String sync);

    @RequestMapping(value = "/reference/sync", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    T findReferenceBySync(@RequestParam(value = "sync", required = false) final String sync);

    @RequestMapping(value = "/syncOrId", method = RequestMethod.GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    T findBySyncOrId(@RequestParam(value = "syncOrId", required = false) final String syncOrId);

    @RequestMapping(value = "/syncs", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    public Set<SyncObjectId> findBySyncs(@RequestParam(required = false, value = "syncs") String[] syncs);

    @RequestMapping(value = "/first", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    T first();

    @RequestMapping(method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    PageableResource list(@RequestParam Map<String, String> allRequestParams);

    @RequestMapping(method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    PageableResource list();

    @RequestMapping(value = "/count", method = GET, consumes = TEXT_PLAIN_VALUE)
    Long count(@RequestParam Map<String, Object> allRequestParams);

    @RequestMapping(value = "/count", method = GET, consumes = TEXT_PLAIN_VALUE)
    Long count();

    @RequestMapping(value = "/lastModify", method = RequestMethod.GET, consumes = TEXT_PLAIN_VALUE)
    Date getLastModify();

    @RequestMapping(value = "/sync/id", method = RequestMethod.GET, consumes = TEXT_PLAIN_VALUE)
    String getIdOfsync(@RequestParam(value = "sync", required = false) final String sync);
}

