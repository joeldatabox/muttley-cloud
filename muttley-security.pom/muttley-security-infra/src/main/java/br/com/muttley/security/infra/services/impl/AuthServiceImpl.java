package br.com.muttley.security.infra.services.impl;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.User;
import br.com.muttley.security.infra.feign.UserServiceClient;
import br.com.muttley.security.infra.services.AuthService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Joel Rodrigues Moreira on 19/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final String tokenHeader;
    private UserServiceClient userServiceClient;

    /**
     * Se tivermos em um contexto de microserviço, o tokenHeader deve ser Authorization-jwt, caso contrario
     * deve ser apenas Authorization
     */
    public AuthServiceImpl(String tokenHeader, UserServiceClient userServiceClient) {
        this.tokenHeader = tokenHeader;
        this.userServiceClient = userServiceClient;
    }

    @Override
    public Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public JwtUser getCurrentJwtUser() {
        return (JwtUser) getCurrentAuthentication().getPrincipal();
    }

    /**
     * Retorna o token corrente da requisição
     */
    @Override
    public JwtToken getCurrentToken() {
        return new JwtToken(
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest()
                        .getHeader(this.tokenHeader)
        );
    }

    @Override
    public User getCurrentUser() {
        return getCurrentJwtUser().getOriginUser();
    }

    /**
     * Retorna um usuário atravez do nome de usuário
     *
     * @param username pode ser um nome ou um email
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return new JwtUser(this.userServiceClient.findByUserName(username));
    }
}
