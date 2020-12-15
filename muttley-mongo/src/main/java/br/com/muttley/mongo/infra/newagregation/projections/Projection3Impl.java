package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.mongo.infra.newagregation.paramvalue.NewQueryParam;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.LinkedList;
import java.util.List;

import static br.com.muttley.mongo.infra.newagregation.projections.Criterion3.CriterionBuilder.from;

/**
 * @author Joel Rodrigues Moreira on 10/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class Projection3Impl implements Projection3 {
    private final List<Criterion3> criterions;

    protected Projection3Impl() {
        this.criterions = new LinkedList<>();
    }

    @Override
    public Projection3 addParam(NewQueryParam param) {
        //this.criterions.add(from(param));
        return this;
    }

    @Override
    public List<AggregationOperation> getAggregations() {
        return null;
    }

    @Override
    public List<Criteria> getCriteria() {
        return null;
    }

    @Override
    public List<AggregationOperation> getQuery() {
        return null;
    }
}
