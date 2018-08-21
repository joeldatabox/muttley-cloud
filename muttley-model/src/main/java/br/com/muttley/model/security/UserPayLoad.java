package br.com.muttley.model.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author Joel Rodrigues Moreira on 23/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserPayLoad implements Serializable {

    @NotBlank(message = "O campo nome não pode ser nulo!")
    @Size(min = 4, max = 200, message = "O campo nome deve ter de 4 a 200 caracteres!")
    private String name;
    @NotBlank(message = "Informe um email válido!")
    @Email(message = "Informe um email válido!")
    private String email;
    @NotBlank(message = "Informe uma senha valida!")
    private String passwd;

    @JsonCreator
    public UserPayLoad(
            @JsonProperty("name") final String name,
            @JsonProperty("email") final String email,
            @JsonProperty("passwd") final String passwd) {
        this.name = name;
        this.email = email;
        this.passwd = passwd;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswd() {
        return passwd;
    }
}
