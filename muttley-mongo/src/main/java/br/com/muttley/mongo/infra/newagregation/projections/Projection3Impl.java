package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.mongo.infra.newagregation.paramvalue.NewQueryParam;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

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
        return this.criterions
                .stream()
                .map(it -> this.extractAggregations(it))
                .flatMap(Collection::stream)
                .collect(toCollection(LinkedList::new));
    }

    @Override
    public List<Criteria> getCriteria() {
        return this.criterions
                .stream()
                .map(it -> it.extractCriteria())
                .flatMap(Collection::stream)
                .collect(toCollection(LinkedList::new));
    }

    @Override
    public List<AggregationOperation> getQuery() {
        return this.criterions
                .stream()
                .map(it ->
                        concat(
                                this.extractAggregations(it).stream(),
                                it.extractCriteria()
                                        .stream()
                                        .map(iit -> match(iit))
                        ).collect(toCollection(LinkedList::new))
                ).flatMap(Collection::stream)
                .collect(toCollection(LinkedList::new));
    }

    @Override
    public Projection3 addCriterion(Criterion3 criterion) {
        this.criterions.add(criterion);
        return this;
    }

    private List<AggregationOperation> extractAggregations(final Criterion3 criterion) {
        if (!CollectionUtils.isEmpty(criterion.getSubcriterions())) {
            return new LinkedList<>(
                    criterion.getSubcriterions()
                            .stream()
                            .map(it -> this.extractAggregations(it))
                            .flatMap(Collection::stream)
                            .collect(toList())
            );
        } else {
            return new LinkedList<>(criterion.extractAgregations());
        }
    }

}
