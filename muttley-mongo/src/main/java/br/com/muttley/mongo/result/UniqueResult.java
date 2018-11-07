package br.com.muttley.mongo.result;

/**
 * @author Joel Rodrigues Moreira on 06/11/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
public class UniqueResult {
    private Object result;

    public Object getResult() {
        return result;
    }

    public UniqueResult setResult(Object result) {
        this.result = result;
        return this;
    }
}
