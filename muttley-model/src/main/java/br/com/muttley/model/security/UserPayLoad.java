package br.com.muttley.model.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 23/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserPayLoad implements Serializable {

    @NotBlank(message = "O campo nome não pode ser nulo!")
    @Size(min = 4, max = 200, message = "O campo nome deve ter de 4 a 200 caracteres!")
    private String name;
    private String description;
    @Email(message = "Informe um email válido!")
    private String email;
    private String userName;
    private Set<String> nickUsers;
    @NotBlank(message = "Informe uma senha valida!")
    private String passwd;

    @JsonCreator
    public UserPayLoad(
            @JsonProperty("name") final String name,
            @JsonProperty("description") final String description,
            @JsonProperty("email") final String email,
            @JsonProperty("userName") final String userName,
            @JsonProperty("nickUsers") final Set<String> nickUsers,
            @JsonProperty("passwd") final String passwd) {
        this.name = name;
        this.description = description;
        this.email = email;
        this.userName = userName;
        this.nickUsers = nickUsers;
        this.passwd = passwd;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public Set<String> getNickUsers() {
        return nickUsers;
    }

    public String getPasswd() {
        return passwd;
    }
}
