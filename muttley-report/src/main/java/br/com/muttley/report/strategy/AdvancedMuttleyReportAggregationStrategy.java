package br.com.muttley.report.strategy;

import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.LinkedList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;

/**
 * @author Joel Rodrigues Moreira on 13/07/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class AdvancedMuttleyReportAggregationStrategy implements MuttleyReportAggregationStrategy {
    private final List<AggregationOperation> before;
    private final List<AggregationOperation> after;

    public AdvancedMuttleyReportAggregationStrategy(List<AggregationOperation> before, List<AggregationOperation> after) {
        this.before = before;
        this.after = after;
    }

    @Override
    public List<AggregationOperation> getAggregation(long skip, long limit) {
        final List<AggregationOperation> operations = new LinkedList<>(this.before);
        operations.add(skip(skip));
        operations.add(limit(limit));
        operations.addAll(this.after);
        return operations;
    }
}
