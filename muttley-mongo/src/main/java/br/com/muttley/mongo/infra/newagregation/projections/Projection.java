package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.newagregation.paramvalue.QueryParam;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static br.com.muttley.mongo.infra.newagregation.operators.Operator.COUNT;

/**
 * @author Joel Rodrigues Moreira on 10/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Projection {
    Projection addParam(final QueryParam param);

    List<AggregationOperation> getAggregations();

    List<Criteria> getCriteria();

    List<AggregationOperation> getQuery();

    Projection addCriterion(Criterion criterion);

    public static class Builder {
        private EntityMetaData entityMetaData;
        private List<QueryParam> queryParamsFirsts;
        private List<QueryParam> queriesParams;
        private boolean addCountParam = false;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder withEntityMetadata(final EntityMetaData entityMetaData) {
            this.entityMetaData = entityMetaData;
            return this;
        }

        public Builder addQueryParamFirst(final QueryParam queryParam) {
            if (this.queryParamsFirsts == null) {
                this.queryParamsFirsts = new LinkedList<>();
            }
            this.queryParamsFirsts.add(queryParam);
            return this;
        }

        public Builder withQueriesParams(final List<QueryParam> queriesParams) {
            this.queriesParams = queriesParams;
            return this;
        }

        public Builder withURL(final String url) {
            return this.withQueriesParams(QueryParam.BuilderFromURL.newInstance().fromURL(url).build());
        }

        public Projection build() {
            final ProjectionMetadata metadata = ProjectionMetadata.ProjectionMetadataBuilder.build(entityMetaData);
            final Projection projection = new ProjectionImpl(metadata);

            //garantindo que não teremos uma lista que queries vazia
            if (!CollectionUtils.isEmpty(this.queriesParams)) {
                this.queriesParams = new LinkedList<>();
            }
            //adicionar os parametros iniciais necessarios
            if (!CollectionUtils.isEmpty(this.queryParamsFirsts)) {
                this.queryParamsFirsts.stream().sorted(Collections.reverseOrder())
                        .forEach(it -> this.queriesParams.add(0, it));
            }
            //adicionando o parametro para contagem
            if (this.addCountParam && (!COUNT.getWildcard().equals(this.queriesParams.get(this.queriesParams.size() - 1).getKey()))) {
                this.queriesParams.add(
                        QueryParam.Builder
                                .newInstance()
                                .withKey(COUNT.getWildcard())
                                .withValue(null)
                                .build()
                );
            }
            this.queriesParams.forEach(it -> projection.addCriterion(Criterion.CriterionBuilder.from(metadata, it)));
            return projection;
        }

        /**
         * Adiciona o parametro para contagem, caso não tenha sido inserido no final
         */
        public Builder addCountParamEndsIfNotExists() {
            this.addCountParam = true;
            return this;
        }
    }
}
