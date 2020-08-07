package br.com.muttley.security.infra.server;


import br.com.muttley.feign.service.MuttleyPropagateHeadersService;
import br.com.muttley.feign.service.interceptors.PropagateHeadersInterceptor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static br.com.muttley.security.infra.properties.Properties.TOKEN_HEADER;
import static br.com.muttley.security.infra.properties.Properties.TOKEN_HEADER_JWT;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Configuração necessária do feign para consumo de dados do serviço de segurança
 */
@Configuration
public class FeignClientConfig {

    @Bean
    public BasicAuthorizationJWTRequestInterceptor createBasicAuthRequestInterceptor(
            @Value("${muttley.security-server.user.name}") final String userNameServer,
            @Value("${muttley.security-server.user.password}") final String userPasswordServer,
            @Value(TOKEN_HEADER_JWT) final String tokenHeaderJwt,
            @Value(TOKEN_HEADER) final String tokenHeader) {
        return new BasicAuthorizationJWTRequestInterceptor(userNameServer, userPasswordServer, tokenHeaderJwt, tokenHeader);
    }

    @Bean
    public PropagateHeadersInterceptor createPropagateHeadersInterceptor(@Autowired final ObjectProvider<MuttleyPropagateHeadersService> muttleyPropagateHeadersService) {
        return new PropagateHeadersInterceptor(muttleyPropagateHeadersService.getIfAvailable());
    }
}
