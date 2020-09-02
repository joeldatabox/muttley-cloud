package br.com.muttley.mongo.infra.operators.impl;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OperatorCriteriaGTE extends AbstractOperatorCriteria<Criteria> {
    private static final String wildcard = ".$gte";

    @Override
    public String getWildcard() {
        return wildcard;
    }

    @Override
    public Criteria extract(EntityMetaData entityMetaData, String key, Object value) {
        final EntityMetaData m = entityMetaData.getFieldByName(key);
        if (m != null) {
            return new Criteria(key).gte(m.converteValue(value));
        }
        return new Criteria(key).gte(value);
    }
}
