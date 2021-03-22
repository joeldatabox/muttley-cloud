package br.com.muttley.model.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Classe para auxiliar a atulização da senha
 */
public class PasswdPayload {

    private JwtToken token;
    @NotNull(message = "Informe a senha atual!")
    @NotEmpty(message = "Informe a senha atual!")
    private String actualPassword;
    @NotNull(message = "Informe uma nova senha valida!")
    @NotEmpty(message = "Informe uma nova senha valida!")
    private String newPassword;

    public PasswdPayload() {
    }

    @JsonCreator
    public PasswdPayload(
            @JsonProperty("token") final JwtToken token,
            @JsonProperty("actualPassword") final String actualPassword,
            @JsonProperty("newPassword") final String newPassword) {
        this.token = token;
        this.actualPassword = actualPassword;
        this.newPassword = newPassword;
    }

    public JwtToken getToken() {
        return token;
    }

    public PasswdPayload setToken(final JwtToken token) {
        this.token = token;
        return this;
    }

    public String getActualPassword() {
        return actualPassword;
    }

    public void setActualPassword(final String actualPassword) {
        this.actualPassword = actualPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(final String newPassword) {
        this.newPassword = newPassword;
    }


}
