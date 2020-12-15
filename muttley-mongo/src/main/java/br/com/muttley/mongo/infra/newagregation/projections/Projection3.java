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
            final Projection3 projection = new Projection3Impl();
            final ProjectionMetadata metadata = ProjectionMetadata.ProjectionMetadataBuilder.build(entityMetaData);
            queriesParam.forEach(entrySet -> {
                //extraindo o nome do campo
                /*final String keyTrimap = Projection2.ProjectionBuilder.replaceAllOperators(entrySet.getKey());

                final Criterion3 criterion3 = Criterion3.CriterionBuilder.from(entrySet);

                projection.add(projection, entityMetaData, keyTrimap, Criterion2.Criterion2Builder.from(entrySet));*/
                /*if (!projection.subpropertiesIsEmpty()) {
                    projection.subproperties.forEach(it -> it.parentEntityMetadata = entityMetaData);
                }*/
            });
            return projection;
        }
    }
}
