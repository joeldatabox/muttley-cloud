package br.com.muttley.security.feign.auth;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Aplica o filtro de autenticação necessario
 */
@RestController
@RequestMapping(value = "/api/v1/users/authentication", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public interface AuthenticationTokenServiceClient {

    @RequestMapping(value = "/user-from-token", method = RequestMethod.POST)
    public JwtUser getUserFromToken(final @RequestBody JwtToken token);
}
