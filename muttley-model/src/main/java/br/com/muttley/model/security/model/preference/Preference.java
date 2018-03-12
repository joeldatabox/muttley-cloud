package br.com.muttley.model.security.model.preference;

import com.google.common.base.Objects;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 12/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class Preference {
    protected String key;
    protected Object value;

    public Preference() {
    }

    public Preference(final String key, final Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Preference)) return false;
        final Preference that = (Preference) o;
        return Objects.equal(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key, 2, 3);
    }

    /**
     * @return true se a key não for nula nem for uma String vazia
     */
    protected boolean isValid() {
        return !isEmpty(this.key);
    }
}
