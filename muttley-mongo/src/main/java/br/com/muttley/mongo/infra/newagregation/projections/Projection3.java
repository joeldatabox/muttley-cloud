package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.newagregation.paramvalue.NewQueryParam;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 10/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Projection3 {
    Projection3 addParam(final NewQueryParam param);

    List<AggregationOperation> getAggregations();

    List<Criteria> getCriteria();

    List<AggregationOperation> getQuery();

    public static class ProjectionBuilder {
        private ProjectionBuilder() {
        }

        public static Projection3 from(EntityMetaData entityMetaData, List<NewQueryParam> queriesParam) {
            final ProjectionMetadata metadata = ProjectionMetadata.ProjectionMetadataBuilder.build(entityMetaData);
            final Projection3 projection = new Projection3Impl(metadata);
            queriesParam.forEach(it -> projection.addCriterion(Criterion3.CriterionBuilder.from(metadata, it)));
            return projection;
        }
    }

    Projection3 addCriterion(Criterion3 criterion);
}
