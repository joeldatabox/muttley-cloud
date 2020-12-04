package br.com.muttley.mongo.infra.test.projections;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.operators.Operator;
import br.com.muttley.mongo.infra.operators.Operator2;
import br.com.muttley.mongo.infra.test.url.paramvalue.NewQueryParam;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Joel Rodrigues Moreira on 02/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Projection2 {
    Projection2 addProjection(final Projection2 projection, final EntityMetaData entityMetaData, final String property, final Criterion2 criterion);

    List<AggregationOperation> getAggregations();

    List<Criteria> getCriteria();

    List<AggregationOperation> getQuery();

    /**
     * Retorna uma query de consulta com agregações e tudo que for necessário incluindo o where
     */
    //List<AggregationOperation> getQueryProjection();


    public static class ProjectionBuilder {
        private ProjectionBuilder() {
        }

        public static Projection2 from(EntityMetaData entityMetaData, List<NewQueryParam> queriesParam) {
            final Projection2Impl projection = new Projection2Impl();
            queriesParam.stream().forEach(entrySet -> {
                //extraindo o nome do campo
                Operator2 operator = Operator2.of(entrySet.getKey());
                final String keyTrimap = Projection2.ProjectionBuilder.replaceAllOperators(entrySet.getKey());

                projection.addProjection(projection, entityMetaData, keyTrimap, new Criterion2Impl(operator, entrySet.getValue()));
                /*if (!projection.subpropertiesIsEmpty()) {
                    projection.subproperties.forEach(it -> it.parentEntityMetadata = entityMetaData);
                }*/
            });
            return projection;
        }

        /**
         * Remove qualquer operador presente em uma string
         * por exemplo:
         *
         * @param value => "pessoa.nome.$is"
         * @return "pessoa.nome"
         */
        private static String replaceAllOperators(final String value) {
            if (!value.contains(".$")) {
                return value;
            }
            String result = value;
            //pegando todos os operadores
            final String[] operators = Stream.of(Operator.values())
                    //pegando a representação em string
                    .map(Operator::toString)
                    .toArray(String[]::new);
            for (final String widcard : operators) {
                result = result.replace(widcard, "");
            }
            return result;
        }
    }
}
