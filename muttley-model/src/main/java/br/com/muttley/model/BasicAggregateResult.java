package br.com.muttley.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @author Joel Rodrigues Moreira on 19/06/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@EqualsAndHashCode(of = "result")
public class BasicAggregateResult<T> {
    private T result;

    public T getResult() {
        return result;
    }

    public BasicAggregateResult<T> setResult(final T result) {
        this.result = result;
        return this;
    }
}
