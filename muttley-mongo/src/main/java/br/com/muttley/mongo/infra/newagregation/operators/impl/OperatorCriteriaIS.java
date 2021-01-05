package br.com.muttley.mongo.infra.newagregation.operators.impl;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OperatorCriteriaIS extends AbstractOperatorCriteria<Criteria> {
    public static final String wildcard = ".$is";

    @Override
    public String getWildcard() {
        return wildcard;
    }

    @Override
    public Criteria extract(final EntityMetaData entityMetaData, final String key, final Object value) {
        return this.extract(entityMetaData, key, key, value);
    }

    @Override
    public Criteria extract(EntityMetaData entityMetaData, String compositePropertyWithFather, String key, Object value) {
        final EntityMetaData m = entityMetaData.getFieldByName(key);
        if (m != null) {
            return new Criteria(compositePropertyWithFather).is(m.converteValue(value));
        }
        return new Criteria(compositePropertyWithFather).is(value);
    }
}
