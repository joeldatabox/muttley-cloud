package br.com.muttley.model.validation;

import br.com.muttley.model.Model;
import lombok.Getter;

/**
 * @author Joel Rodrigues Moreira on 13/01/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
public class Reference<T extends Model> {
    private String id;
    private T value;

    public Reference(String id) {
        this.id = id;
    }

    public Reference(T value) {
        this(value.getId());
        this.value = value;
    }

    public boolean modelIsNull() {
        return this.value == null;
    }
}
