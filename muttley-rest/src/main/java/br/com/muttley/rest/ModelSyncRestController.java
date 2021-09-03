package br.com.muttley.rest;

import br.com.muttley.model.ModelSync;
import br.com.muttley.model.jackson.DefaultDateFormatConfig;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Joel Rodrigues Moreira on 06/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface ModelSyncRestController<T extends ModelSync> extends RestResource<T>, RestController<T> {
    @RequestMapping(value = "/sync/{sync}", method = RequestMethod.PUT, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity updateBySync(@PathVariable("sync") String sync, @RequestBody T model);

    @RequestMapping(value = "/sync/{sync}", method = RequestMethod.DELETE, consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE}, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity delteBySync(@PathVariable("sync") String sync);

    @RequestMapping(value = "/sync/{sync}", method = RequestMethod.GET, consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE}, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity findBySync(@PathVariable("sync") final String sync, final HttpServletResponse response);

    @RequestMapping(value = "/reference/sync/{sync}", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    ResponseEntity findReferenceBySync(@PathVariable("sync") final String sync, final HttpServletResponse response);

    @RequestMapping(value = "/syncOrId/{syncOrId}", method = RequestMethod.GET, consumes = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE}, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity findBySyncOrId(@PathVariable("syncOrId") final String syncOrId, final HttpServletResponse response);

    @RequestMapping(value = "/syncs", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public ResponseEntity findBySyncs(@RequestParam(required = false, value = "syncs") String[] syncs, HttpServletResponse response);

    @RequestMapping(value = "/synchronization", method = RequestMethod.PUT, produces = {APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity synchronization(@RequestBody final List<T> values);

    @RequestMapping(value = "/lastModify", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity getLastModify(@Autowired final DefaultDateFormatConfig dateFormat);
}
