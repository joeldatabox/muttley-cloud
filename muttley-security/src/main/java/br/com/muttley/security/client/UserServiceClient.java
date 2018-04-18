package br.com.muttley.security.client;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Passwd;
import br.com.muttley.model.security.User;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security.name-server}", url = "/api/v1/users", configuration = FeignClientConfig.class)
public interface UserServiceClient extends RestControllerClient<User> {

    @Override
    @RequestMapping(value = "/{email}", method = PUT, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    User update(@PathVariable("email") String email, @RequestBody User model);

    @RequestMapping(value = "/passwd", method = PUT, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public void updatePasswd(@RequestBody final Passwd passwd);

    @Deprecated
    @RequestMapping(value = "/ad$/{id}", method = DELETE, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    void deleteById(@PathVariable("id") String id);

    @RequestMapping(value = "/{email}", method = DELETE, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    void deleteByEmail(@PathVariable("email") String email);

    @Deprecated
    @RequestMapping(value = "/ad$/{id}", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    User findById(@PathVariable("id") String id);

    @RequestMapping(value = "/{email}", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    User findByEmail(@PathVariable("email") String id);

    @RequestMapping(value = "/user-from-token", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    @ResponseStatus(OK)
    public User getUserFromToken(@RequestBody final JwtToken token);
}
