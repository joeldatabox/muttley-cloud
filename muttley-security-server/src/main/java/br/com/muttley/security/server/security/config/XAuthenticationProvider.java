package br.com.muttley.security.server.security.config;

import br.com.muttley.exception.throwables.security.MuttleySecurityUserNameOrPasswordInvalidException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 17/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class XAuthenticationProvider implements AuthenticationProvider {
    private final String userName;
    private final String passWord;

    public XAuthenticationProvider(@Value("${muttley.security-server.userName.name}") final String userName, @Value("${muttley.security-server.userName.password}") final String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        if (!(this.userName.equals(authentication.getPrincipal()) && this.passWord.equals(authentication.getCredentials()))) {
            throw new MuttleySecurityUserNameOrPasswordInvalidException();
        }
        return authentication;
    }

    @Override
    public boolean supports(final Class<?> aClass) {
        return Authentication.class.isAssignableFrom(aClass);
    }
}
