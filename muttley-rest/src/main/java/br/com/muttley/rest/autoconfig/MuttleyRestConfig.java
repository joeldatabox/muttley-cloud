package br.com.muttley.rest.autoconfig;

import br.com.muttley.rest.service.listenerEventsHateoas.PaginatedResultsRetrievedDiscoverabilityListener;
import br.com.muttley.rest.service.listenerEventsHateoas.ResourceCreatedListener;
import br.com.muttley.rest.service.listenerEventsHateoas.SingleResourceRetrievedDiscoverabilityListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 17/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
public class MuttleyRestConfig {

    @Bean
    @ConditionalOnMissingBean
    public PaginatedResultsRetrievedDiscoverabilityListener paginatedResultsRetrievedDiscoverabilityListenerFactory() {
        return new PaginatedResultsRetrievedDiscoverabilityListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public ResourceCreatedListener resourceCreatedListenerFactory() {
        return new ResourceCreatedListener();
    }

    @Bean
    @ConditionalOnMissingBean
    public SingleResourceRetrievedDiscoverabilityListener singleResourceRetrievedDiscoverabilityListenerFactory() {
        return new SingleResourceRetrievedDiscoverabilityListener();
    }
}
