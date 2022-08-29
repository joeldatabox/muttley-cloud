package br.com.muttley.model;

import lombok.EqualsAndHashCode;

/**
 * @author Joel Rodrigues Moreira on 19/06/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@EqualsAndHashCode(of = "result")
public class BasicAggregateResultCount extends BasicAggregateResult<Long> {
    public static final BasicAggregateResultCount ZERO = new BasicAggregateResultCount(0l);

    public BasicAggregateResultCount() {
    }

    public BasicAggregateResultCount(final long value) {
        this.setResult(value);
    }
}
