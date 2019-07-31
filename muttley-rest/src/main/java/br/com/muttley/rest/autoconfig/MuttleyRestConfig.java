package br.com.muttley.rest.autoconfig;

import br.com.muttley.metadata.headers.HeaderAuthorizationJWT;
import br.com.muttley.metadata.headers.HeaderUserAgent;
import br.com.muttley.rest.service.listenerEventsHateoas.PaginatedResultsRetrievedDiscoverabilityListener;
import br.com.muttley.rest.service.listenerEventsHateoas.ResourceCreatedListener;
import br.com.muttley.rest.service.listenerEventsHateoas.SingleResourceRetrievedDiscoverabilityListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

/**
 * @author Joel Rodrigues Moreira on 17/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
public class MuttleyRestConfig {

    @Bean
    public PaginatedResultsRetrievedDiscoverabilityListener paginatedResultsRetrievedDiscoverabilityListenerFactory() {
        return new PaginatedResultsRetrievedDiscoverabilityListener();
    }

    @Bean
    public ResourceCreatedListener resourceCreatedListenerFactory() {
        return new ResourceCreatedListener();
    }

    @Bean
    public SingleResourceRetrievedDiscoverabilityListener singleResourceRetrievedDiscoverabilityListenerFactory() {
        return new SingleResourceRetrievedDiscoverabilityListener();
    }

    @Bean
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    public HeaderAuthorizationJWT headerAuthorizationJWTFactory(@Value("${muttley.security.jwt.controller.token-header-jwt:Authorization-jwt}") String tokenHeader, @Autowired final HttpServletRequest request) {
        return new HeaderAuthorizationJWT(tokenHeader, request);
    }

    @Bean(name = "userAgente")
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    public HeaderUserAgent headerUserAgentFactory(@Autowired final HttpServletRequest request) {
        return new HeaderUserAgent(request);
    }
}
