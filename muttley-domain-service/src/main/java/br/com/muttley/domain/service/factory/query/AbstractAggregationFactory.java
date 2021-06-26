package br.com.muttley.domain.service.factory.query;

import br.com.muttley.model.security.User;
import br.com.muttley.security.infra.services.AuthService;
import org.springframework.data.mongodb.core.aggregation.Aggregation;


/**
 * @author Joel Rodrigues Moreira on 25/06/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Interface básica para armazenar a implementação básica uma fábrica de query
 */
public abstract class AbstractAggregationFactory {
    public abstract Aggregation factory(final AuthService authService, final User user);
}
