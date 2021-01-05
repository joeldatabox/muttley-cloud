package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.mongo.infra.newagregation.paramvalue.NewQueryParam;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 10/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class Projection3Impl implements Projection3 {
    private final ProjectionMetadata metadata;
    private final List<Criterion3> criterions;
    private String property;
    private String compositePropertyWithFather = "";//nome da propriedade pai em cascata

    protected Projection3Impl(final ProjectionMetadata metadata) {
        this.metadata = metadata;
        this.criterions = new LinkedList<>();
    }

    @Override
    public Projection3 addParam(NewQueryParam param) {
        this.criterions.add(Criterion3.CriterionBuilder.from(this.metadata, param));
        return this;
    }

    @Override
    public List<AggregationOperation> getAggregations() {
        final List<AggregationOperation> aggregationOperations = new LinkedList<>();
        this.criterions.forEach(it -> {
            aggregationOperations.addAll(this.extractAggregations(it));
        });
        return aggregationOperations;
    }

    @Override
    public List<Criteria> getCriteria() {
        return null;
    }

    @Override
    public List<AggregationOperation> getQuery() {
        return null;
    }

    @Override
    public Projection3 addCriterion(Criterion3 criterion) {
        this.criterions.add(criterion);
        return this;
    }

    private List<AggregationOperation> extractAggregations(final Criterion3 criterion) {
        final List<AggregationOperation> aggregations = new LinkedList<>();
        if (!CollectionUtils.isEmpty(criterion.getSubcriterions())) {
            aggregations.addAll(criterion.getSubcriterions().stream().map(it -> this.extractAggregations(it)).reduce((acc, others) -> {
                acc.addAll(others);
                return acc;
            }).orElse(new LinkedList<>()));
        } else {
            aggregations.addAll(criterion.extractAgregations());
        }
        return aggregations;
    }

}
