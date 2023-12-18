package br.com.muttley.security.feign.auth;

import br.com.muttley.feign.service.config.FeignTimeoutConfig;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Aplica o filtro de autenticação necessario
 */
@FeignClient(value = "${muttley.security.name-server}", path = "/api/v1/users/authentication", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class})
public interface AuthenticationTokenServiceClient {

    @RequestMapping(value = "/user-from-token", method = POST)
    public JwtUser getUserFromToken(final @RequestBody JwtToken token);

    @RequestMapping(value = "/login-by-api-token", method = POST)
    public JwtToken getUserFromToken(@RequestHeader(value = "${muttley.security.jwt.controller.xAPITokenHeader:X-Api-Token}", defaultValue = "") final String tokenValue);
}
