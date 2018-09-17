package br.com.muttley.model.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.io.Serializable;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
public class JwtToken implements Serializable {
    @Getter
    private final String token;

    @JsonCreator
    public JwtToken(@JsonProperty("token") final String token) {
        this.token = token;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return isNullOrEmpty(this.token);
    }
}
