package br.com.muttley.security.feign;

import br.com.muttley.feign.service.config.FeignTimeoutConfig;
import br.com.muttley.feign.service.interceptors.HeadersMetadataInterceptor;
import br.com.muttley.model.security.UserBaseItem;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author Joel Rodrigues Moreira 24/02/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security.name-server}", path = "/api/v1/users-base", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class, HeadersMetadataInterceptor.class})
public interface UserBaseServiceClient {

    @RequestMapping(value = "/userNamesIsAvaliable", method = RequestMethod.GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    boolean userNameIsAvaliable(@RequestParam(value = "userNameFor", required = false) final String userNameFor, @RequestParam(value = "userNames") final Set<String> userNames);

    @RequestMapping(value = "/userByEmailOrUserName", method = RequestMethod.GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    boolean userNameIsAvaliable(@RequestParam(value = "emailOrUsername") final String emailOrUsername);

    @RequestMapping(value = "/create-new-user-and-add", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    void createNewUserAndAdd(@RequestBody final UserBaseItem item);

    @RequestMapping(value = "/merge-user-item-if-exists", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    void mergeUserItemIfExists(@RequestBody final UserBaseItem item);

    @RequestMapping(value = "/add-if-not-exists", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    void addIfNotExists(@RequestBody final UserBaseItem item);

    @RequestMapping(value = "/remove-user/{userName}", method = RequestMethod.DELETE, consumes = APPLICATION_JSON_UTF8_VALUE)
    void removeUser(@PathVariable(value = "userName") final String userName);
}
