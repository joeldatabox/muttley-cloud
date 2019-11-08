package br.com.muttley.security.infra.security.server;

import br.com.muttley.feign.service.interceptors.PropagateHeadersInterceptor;
import br.com.muttley.feign.service.service.MuttleyPropagateHeadersService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
public class FeignClientConfig {

    @Bean
    public BasicAuthorizationJWTRequestInterceptor createBasicAuthRequestInterceptor(
            @Value("${muttley.config-server.security.user.name}") final String userName,
            @Value("${muttley.config-server.security.user.password}") final String passWord) {
        return new BasicAuthorizationJWTRequestInterceptor(userName, passWord);
    }

    @Bean
    public PropagateHeadersInterceptor createPropagateHeadersInterceptor(@Autowired final ObjectProvider<MuttleyPropagateHeadersService> muttleyPropagateHeadersService){
        return new PropagateHeadersInterceptor(muttleyPropagateHeadersService.getIfAvailable());
    }
}