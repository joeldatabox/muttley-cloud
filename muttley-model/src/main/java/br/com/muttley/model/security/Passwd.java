package br.com.muttley.model.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * Classe para auxiliar a atulização da senha
 */
@Getter
@Setter
@Accessors(chain = true)
public class Passwd {

    private JwtToken token;
    @NotNull(message = "Informe a senha atual!")
    @NotEmpty(message = "Informe a senha atual!")
    private String actualPasswd;
    @NotNull(message = "Informe uma nova senha valida!")
    @NotEmpty(message = "Informe uma nova senha valida!")
    private String newPasswd;

    public Passwd() {
    }

    @JsonCreator
    public Passwd(
            @JsonProperty("token") final JwtToken token,
            @JsonProperty("actualPasswd") final String actualPasswd,
            @JsonProperty("newPasswd") final String newPasswd) {
        this.token = token;
        this.actualPasswd = actualPasswd;
        this.newPasswd = newPasswd;
    }
}
