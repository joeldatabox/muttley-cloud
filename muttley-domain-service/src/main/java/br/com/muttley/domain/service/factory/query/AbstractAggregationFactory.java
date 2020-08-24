package br.com.muttley.domain.service.factory.query;

import br.com.muttley.model.security.User;
import br.com.muttley.security.infra.service.AuthService;
import org.springframework.data.mongodb.core.aggregation.Aggregation;

/**
 * @author Joel Rodrigues Moreira 17/08/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Interface básica para armazenar a implementação básica uma fábrica de query
 */
public abstract class AbstractAggregationFactory {
    public abstract Aggregation factory(final AuthService authService, final User user);
}
