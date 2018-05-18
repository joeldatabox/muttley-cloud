package br.com.muttley.security.server.config.security;

import br.com.muttley.security.server.security.config.XAuthenticationFilter;
import br.com.muttley.security.server.security.config.XAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

/**
 * @author Joel Rodrigues Moreira on 17/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
public class SecurityConfig {
    @Bean
    public XAuthenticationProvider createXAuthenticationProvider(@Value("${muttley.security-server.userName.name}") String userName, @Value("${muttley.security-server.userName.password}") String passWord) {
        return new XAuthenticationProvider(userName, passWord);
    }

    @Bean
    @Autowired
    public XAuthenticationFilter createXAuthenticationFilter(final AuthenticationManager authenticationManager) {
        return new XAuthenticationFilter(authenticationManager);
    }
}
