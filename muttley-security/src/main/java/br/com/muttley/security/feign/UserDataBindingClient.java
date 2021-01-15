package br.com.muttley.security.feign;

import br.com.muttley.feign.service.config.FeignTimeoutConfig;
import br.com.muttley.model.security.UserDataBinding;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Joel Rodrigues Moreira 15/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security.name-server}", path = "/api/v1/users-databinding", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class})
public interface UserDataBindingClient {
    @RequestMapping(method = POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    UserDataBinding save(@RequestBody final UserDataBinding value, @RequestParam(required = false, value = "returnEntity", defaultValue = "") final boolean returnEntity);

    @RequestMapping(value = "/{id}", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    void update(@PathVariable("id") final String id, @RequestBody final UserDataBinding model);

    @RequestMapping(method = RequestMethod.GET)
    List<UserDataBinding> list();

    @RequestMapping(value = "/by-username/{userName}", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    List<UserDataBinding> listByUserName(@PathVariable("userName") final String userName);

    @RequestMapping(value = "/by-username/{userName}", method = POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity saveByUserName(
            @RequestParam(required = false, value = "returnEntity", defaultValue = "") final Boolean returnEntity,
            @PathVariable("userName") final String userName,
            @RequestBody final UserDataBinding model);

    @RequestMapping(value = "/by-username/{userName}", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updateByUserName(@PathVariable("userName") final String userName, @RequestBody final UserDataBinding model);

    @RequestMapping(value = "/merge/{userName}", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity merger(@PathVariable("userName") final String userName, @RequestBody final UserDataBinding model);
}
