package br.com.muttley.report.strategy;

import br.com.muttley.report.strategy.MuttleyReportAggregationStrategy;
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
public class SimpleMuttleyReportAggregationStrategy implements MuttleyReportAggregationStrategy {
    private final List<AggregationOperation> operations;

    public SimpleMuttleyReportAggregationStrategy(List<AggregationOperation> operations) {
        this.operations = operations;
    }

    @Override
    public List<AggregationOperation> getAggregation(long skip, long limit) {
        List<AggregationOperation> operations = new LinkedList<>(this.operations);
        operations.add(skip(skip));
        operations.add(limit(limit));
        return operations;
    }
}
