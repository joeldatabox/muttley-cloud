package br.com.muttley.tools;

/**
 * @author Joel Rodrigues Moreira on 08/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyCheck<T> {
    private final T value;

    private Exception ex;

    public MuttleyCheck(final T value) {
        this.value = value;
    }

    public MuttleyCheck<T> orThrows(Exception ex) {
        this.ex = ex;
        return this;
    }

    public T get() {
        return value;
    }
}
