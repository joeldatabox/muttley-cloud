package br.com.muttley.model.security.preference;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 12/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@EqualsAndHashCode(of = "key")
@Getter
public class Preference {
    protected final String key;
    protected final Object value;
    @Transient
    @JsonIgnore
    @Setter
    @Accessors(chain = true)
    protected Object resolved;

    @JsonCreator
    public Preference(
            @JsonProperty("key") final String key,
            @JsonProperty("value") final Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

    @JsonIgnore
    public boolean isResolved() {
        return this.resolved != null;
    }

    /**
     * @return true se a key não for nula nem for uma String vazia
     */
    @JsonIgnore
    public boolean isValid() {
        return !isEmpty(this.key);
    }
}
