package br.com.muttley.security.feign;

import br.com.muttley.feign.service.config.FeignTimeoutConfig;
import br.com.muttley.feign.service.interceptors.HeadersMetadataInterceptor;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.PasswdPayload;
import br.com.muttley.model.security.Password;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserPayLoad;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security.name-server}", path = "/api/v1/users", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class, HeadersMetadataInterceptor.class})
public interface UserServiceClient {

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    public User save(@RequestBody UserPayLoad value, @RequestParam(required = false, value = "returnEntity", defaultValue = "") String returnEntity);

    @RequestMapping(value = "/{userName}", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    public User update(@PathVariable("userName") final String userName, @RequestHeader("${muttley.security.jwt.controller.tokenHeader}") final String token, @RequestBody final User user);

    @RequestMapping(value = "/password", method = PUT)
    public void updatePasswd(@RequestBody final PasswdPayload passwdPayload);

    @RequestMapping(method = DELETE)
    void deleteByUserName(@RequestParam("userName") String userName);

    @Deprecated
    @RequestMapping(value = "/ad$/{id}", method = GET)
    User findById(@PathVariable("id") String id);

    @RequestMapping(method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    User findByUserName(@RequestParam("userName") String userName);

    @RequestMapping(value = "/{id}/password", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    public Password loadPasswordById(@RequestParam("id") final String id);

    @RequestMapping(value = "/user-from-token", method = GET)
    public User getUserFromToken(@RequestBody final JwtToken token);

    @RequestMapping(value = "/email-or-username-or-nickUsers", method = GET)
    public User findUserByEmailOrUserNameOrNickUsers(@RequestParam(value = "email", required = false) final String email, @RequestParam(value = "userName", required = false) final String userName, @RequestParam(value = "nickUsers", required = false) final Set<String> nickUsers);

    @RequestMapping(value = "/exist-email-or-username-or-nickUsers", method = RequestMethod.GET)
    public boolean existUserByEmailOrUserNameOrNickUsers(@RequestParam(value = "email", required = false) final String email, @RequestParam(value = "userName", required = false) final String userName, @RequestParam(value = "nickUsers", required = false) final Set<String> nickUsers);

    @RequestMapping(value = "/userNamesIsAvaliable", method = RequestMethod.GET)
    public boolean userNameIsAvaliable(@RequestParam(value = "userNames") final Set<String> userNames);



}
