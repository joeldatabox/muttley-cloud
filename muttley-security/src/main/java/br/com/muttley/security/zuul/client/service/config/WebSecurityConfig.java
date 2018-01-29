package br.com.muttley.security.zuul.client.service.config;

import br.com.muttley.model.security.service.SecretService;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.infra.component.AuthenticationTokenFilterClient;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.infra.component.util.JwtTokenUtil;
import br.com.muttley.security.infra.repository.UserRepository;
import br.com.muttley.security.infra.service.CacheUserAuthenticationService;
import br.com.muttley.security.infra.service.UserService;
import br.com.muttley.security.infra.service.impl.CacheUserAuthenticationServiceImpl;
import br.com.muttley.security.infra.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configurações dos beans necessários para segurança do client
 *
 * @author Joel Rodrigues Moreira on 18/01/18.
 * @project spring-cloud
 */
@Configuration
public class WebSecurityConfig {
    @Bean
    public UnauthorizedHandler createUnauthorizedHandler(@Value("${springboot.security.jwt.controller.loginEndPoint}") final String urlLogin) {
        return new UnauthorizedHandler(urlLogin);
    }

    @Bean
    @Autowired
    public AuthenticationTokenFilterClient createAuthenticationTokenFilterClient(
            @Value("${springboot.security.jwt.controller.tokenHeader}") final String tokenHeader,
            final CacheUserAuthenticationService cacheAuth,
            final JwtTokenUtil tokenUtil) {
        return new AuthenticationTokenFilterClient(tokenHeader, cacheAuth, tokenUtil);
    }

    @Bean
    @Autowired
    public CacheUserAuthenticationService createCacheUserAuthenticationService(final RedisService redisService) {
        return new CacheUserAuthenticationServiceImpl(redisService);
    }

    @Bean
    @Autowired
    public JwtTokenUtil createJwtTokenUtil(final SecretService secretService) {
        return new JwtTokenUtil(secretService);
    }


    @Bean
    public SecretService createSecretService() {
        return new SecretService();
    }

    @Bean
    @Autowired
    public UserService createUserService(final UserRepository repository, @Value("${springboot.security.jwt.controller.tokenHeader}") final String tokenHeader) {
        return new UserServiceImpl(repository, tokenHeader);
    }

}
