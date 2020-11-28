package br.com.muttley.mongo.infra.operators.impl;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.test.projections.Projection;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 01/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OperatorCriteriaOR extends AbstractOperatorCriteria<Criteria> {
    private static final String wildcard = ".$or";

    @Override
    public String getWildcard() {
        return wildcard;
    }

    @Override
    public Criteria extract(EntityMetaData entityMetaData, String key, Object value) {
/*
        final String text = value.toString();
        if (text == null || text.length() <= 2) {
            throw new MuttleyBadRequestException(null, "$or", "Erro ao executar a consulta").addDetails("$or", "informe algum parametro valido para o operador $or");
        }
        final String[] allCriterions = split(";;", text.substring(1, text.length() - 1));
        final List<AggregationUtils.Pipelines> pipelines = new ArrayList<>();
        for (int i = 0; i < allCriterions.length; i++) {
            final String expr[] = split("=", allCriterions[i]);
            //evitando que algum animal passe uma string vazia
            if (expr.length > 1) {
                //extraindo os criterios do or
                pipelines.addAll(extractCriteria(entityMetaData, generatePipelines, of(expr[0]), replaceAllOperators(expr[0]), expr[1]));
                //criterionsOr[i] = extractCriteria(entityMetaData, of(expr[0]), replaceAllOperators(expr[0]), expr[1]);
            } else {
                //string ta vazia mano
                pipelines.addAll(extractCriteria(entityMetaData, generatePipelines, of(expr[0]), replaceAllOperators(expr[0]), ""));
                //criterionsOr[i] = extractCriteria(entityMetaData, of(expr[0]), replaceAllOperators(expr[0]), "");
            }
        }
        return asList(new AggregationUtils.Pipelines(new Criteria().orOperator(pipelines.stream().map(it -> it.getCriteria()).toArray(Criteria[]::new))));
        //return pipelines;*/
        throw new NotImplementedException();
    }

    //Essa porra deve retornar uma lista de projeção
    @Override
    public Criteria extract(EntityMetaData entityMetaData, String compositePropertyWithFather, String key, Object value) {
        final Projection p = Projection.ProjectionBuilder.from(entityMetaData, null);
        p.get
        throw new NotImplementedException();
    }
}
