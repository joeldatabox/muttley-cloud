package br.com.muttley.security.feign.auth;

import br.com.muttley.feign.autoconfig.FeignTimeoutConfig;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.UserPayLoadLogin;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security-server.name-server}", path = "/api/v1/users/authentication", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class})
public interface AuthenticationRestServiceClient {

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public JwtToken createAuthenticationToken(@RequestBody final UserPayLoadLogin payload);

    @RequestMapping(value = "/refresh", method = POST)
    public JwtToken refreshAndGetAuthenticationToken(@RequestBody JwtToken token);
}
