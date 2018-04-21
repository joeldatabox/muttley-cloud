package br.com.muttley.security.feign;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Passwd;
import br.com.muttley.model.security.User;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security.name-server}", path = "/api/v1/users", configuration = FeignClientConfig.class)
public interface UserServiceClient {

    @RequestMapping(method = POST, consumes = {APPLICATION_JSON_UTF8_VALUE})
    public User save(@RequestBody User value, @RequestParam(required = false, value = "returnEntity", defaultValue = "") String returnEntity);

    @RequestMapping(value = "/{email}", method = PUT, consumes = {APPLICATION_JSON_UTF8_VALUE})
    public User update(@PathVariable("email") final String email, @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") final String token, @RequestBody final User user);

    @RequestMapping(value = "/passwd", method = PUT)
    public void updatePasswd(@RequestBody final Passwd passwd);

    @RequestMapping(value = "/{email}", method = DELETE)
    void deleteByEmail(@PathVariable("email") String email);

    @Deprecated
    @RequestMapping(value = "/ad$/{id}", method = GET)
    User findById(@PathVariable("id") String id);

    @RequestMapping(value = "/{email}", method = GET)
    User findByEmail(@PathVariable("email") String id);

    @RequestMapping(value = "/user-from-token", method = GET)
    public User getUserFromToken(@RequestBody final JwtToken token);
}
