package br.com.muttley.model.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Bean para facilitar no processo de login
 */
@Getter
public class UserPayLoadLogin {
    private final String username;
    private final String password;

    @JsonCreator
    public UserPayLoadLogin(
            @JsonProperty("username") final String username,
            @JsonProperty("password") final String password) {
        this.username = username;
        this.password = password;
    }
}
