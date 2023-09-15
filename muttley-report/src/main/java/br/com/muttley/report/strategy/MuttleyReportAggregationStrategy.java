package br.com.muttley.report.strategy;

import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 13/07/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyReportAggregationStrategy {

    List<AggregationOperation> getAggregation(final long skip, final long limit);
}
