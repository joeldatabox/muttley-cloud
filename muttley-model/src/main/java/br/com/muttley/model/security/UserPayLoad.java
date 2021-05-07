package br.com.muttley.model.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(of = {"email", "userName"})
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
    //private Set<UserDataBinding> dataBindings;

    @JsonCreator
    public UserPayLoad(
            @JsonProperty("name") final String name,
            @JsonProperty("description") final String description,
            @JsonProperty("email") final String email,
            @JsonProperty("userName") final String userName,
            @JsonProperty("nickUsers") final Set<String> nickUsers,
            @JsonProperty("passwd") final String passwd
            /*@JsonProperty("dataBindings") final Set<UserDataBinding> dataBindings*/) {
        this.name = name;
        this.description = description;
        this.email = email;
        this.userName = userName;
        this.nickUsers = nickUsers;
        this.passwd = passwd;
        //this.dataBindings = dataBindings;
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

    public static final class Builder {
        private String name;
        private String description;
        private String email;
        private String userName;
        private Set<String> nickUsers;
        private String passwd;

        private Builder() {
        }

        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(final String description) {
            this.description = description;
            return this;
        }

        public Builder setEmail(final String email) {
            this.email = email;
            return this;
        }

        public Builder setUserName(final String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setNickUsers(final Set<String> nickUsers) {
            this.nickUsers = nickUsers;
            return this;
        }

        public Builder setPasswd(final String passwd) {
            this.passwd = passwd;
            return this;
        }

        public Builder set(final User user) {
            return this.setName(user.getName())
                    .setDescription(user.getDescription())
                    .setEmail(user.getEmail())
                    .setUserName(user.getUserName())
                    .setNickUsers(user.getNickUsers());
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public UserPayLoad build() {
            return new UserPayLoad(this.name, this.description, this.email, this.userName, this.nickUsers, this.passwd);
        }
    }
}
