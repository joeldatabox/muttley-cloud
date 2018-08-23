package br.com.muttley.security.infra.security.server;

import br.com.muttley.security.properties.MuttleySecurityProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
public class FeignClientConfig {

    @Autowired
    private MuttleySecurityProperty property;

    @Bean
    public BasicAuthorizationJWTRequestInterceptor createBasicAuthRequestInterceptor() {
        return new BasicAuthorizationJWTRequestInterceptor(property.getSecurityServer().getUser().getName(), property.getSecurityServer().getUser().getPassword());
    }
}
