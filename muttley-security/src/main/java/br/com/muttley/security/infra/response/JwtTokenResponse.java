package br.com.muttley.security.infra.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
public class JwtTokenResponse implements Serializable {
    private final String token;

    @JsonCreator
    public JwtTokenResponse(@JsonProperty("token") final String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }
}
