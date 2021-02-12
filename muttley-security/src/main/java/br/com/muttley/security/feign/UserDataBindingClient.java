package br.com.muttley.security.feign;

import br.com.muttley.feign.service.config.FeignTimeoutConfig;
import br.com.muttley.model.security.KeyUserDataBinding;
import br.com.muttley.model.security.UserData;
import br.com.muttley.model.security.UserDataBinding;
import br.com.muttley.model.security.expanders.KeyUserDataBindingExpander;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import feign.Param;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
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
    UserDataBinding saveByUserName(
            @RequestParam(required = false, value = "returnEntity", defaultValue = "") final Boolean returnEntity,
            @PathVariable("userName") final String userName,
            @RequestBody final UserDataBinding model);

    @RequestMapping(value = "/by-username/{userName}", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    UserDataBinding updateByUserName(@PathVariable("userName") final String userName, @RequestBody final UserDataBinding model);

    @RequestMapping(value = "/merge/{userName}", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    void merger(@PathVariable("userName") final String userName, @RequestBody final UserDataBinding model);

    @RequestMapping(value = "/key/{key}", method = RequestMethod.GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    UserDataBinding getKey(@PathVariable("key") final KeyUserDataBinding key);

    @RequestMapping(value = "/key/{key}", method = RequestMethod.GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    UserDataBinding getKey(@PathVariable("key") final String key);

    @RequestMapping(value = "/by-username/{userName}/key/{key}", method = RequestMethod.GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    UserDataBinding getKeyByUserName(@PathVariable("userName") final String userName, @PathVariable("key") final KeyUserDataBinding key);

    @RequestMapping(value = "/by-username/{userName}/key/{key}", method = RequestMethod.GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    UserDataBinding getKeyByUserName(@PathVariable("userName") final String userName, @PathVariable("key") final String key);

    @RequestMapping(value = "/contains/key/{key}", method = RequestMethod.GET, consumes = TEXT_PLAIN_VALUE)
    boolean contains(@PathVariable("key") final KeyUserDataBinding key);

    @RequestMapping(value = "/contains/key/{key}", method = RequestMethod.GET, consumes = TEXT_PLAIN_VALUE)
    boolean contains(@PathVariable("key") final String key);

    @RequestMapping(value = "/by-username/{userName}/contains/key/{key}", method = RequestMethod.GET, consumes = TEXT_PLAIN_VALUE)
    boolean containsByUserName(@PathVariable("userName") final String userName, @PathVariable("key") final KeyUserDataBinding key);

    @RequestMapping(value = "/by-username/{userName}/contains/key/{key}", method = RequestMethod.GET, consumes = TEXT_PLAIN_VALUE)
    boolean containsByUserName(@PathVariable("userName") final String userName, @PathVariable("key") final String key);

    @RequestMapping(value = "/user-by", method = RequestMethod.GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    UserData getUserBy(@Param(value = "key", expander = KeyUserDataBindingExpander.class) final KeyUserDataBinding key, @RequestParam(required = false, value = "value", defaultValue = "") final String value);

    @RequestMapping(value = "/user-by", method = RequestMethod.GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    UserData getUserBy(@RequestParam(required = false, value = "key", defaultValue = "") final String key, @RequestParam(required = false, value = "value", defaultValue = "") final String value);
}
