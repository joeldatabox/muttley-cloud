package br.com.muttley.model.security;

import br.com.muttley.model.security.preference.Foto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.util.ObjectUtils;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
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
    private Foto foto;
    private Set<String> nickUsers;
    @NotBlank(message = "Informe uma senha valida!")
    private String passwd;
    private String fone;
    private boolean odinUser = false;

    //numero enviado por sms para confirma o numero telefonico
    private String seedVerification;

    //numero que mobilidade receber e que deve ser comparado com o seed
    private String codeVerification;

    private boolean renewCode = false;
    //private Set<UserDataBinding> dataBindings;

    @JsonCreator
    public UserPayLoad(
            @JsonProperty("name") final String name,
            @JsonProperty("description") final String description,
            @JsonProperty("email") final String email,
            @JsonProperty("userName") final String userName,
            @JsonProperty("foto") final Foto foto,
            @JsonProperty("nickUsers") final Set<String> nickUsers,
            @JsonProperty("passwd") final String passwd,
            @JsonProperty("fone") String fone,
            @JsonProperty("odinUser") boolean odinUser,
            @JsonProperty("seedVerification") String seedVerification,
            @JsonProperty("codeVerification") String codeVerification,
            @JsonProperty("renewCode") boolean renewCode

            /*@JsonProperty("dataBindings") final Set<UserDataBinding> dataBindings*/) {
        this.name = name;
        this.description = description;
        this.email = email;
        this.userName = userName;
        this.foto = foto;
        this.nickUsers = nickUsers;
        this.passwd = passwd;
        this.fone = fone;
        this.odinUser = odinUser;
        this.seedVerification = seedVerification;
        this.codeVerification = codeVerification;
        this.renewCode = renewCode;
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

    public Foto getFoto() {
        return foto;
    }

    public Set<String> getNickUsers() {
        return nickUsers;
    }

    public String getPasswd() {
        return passwd;
    }

    public String getFone() {
        return fone;
    }

    public UserPayLoad setFone(String fone) {
        this.fone = fone;
        return this;
    }

    public boolean isOdinUser() {
        return odinUser;
    }

    public UserPayLoad setOdinUser(boolean odinUser) {
        this.odinUser = odinUser;
        return this;
    }

    public String getSeedVerification() {
        return seedVerification;
    }

    public UserPayLoad setSeedVerification(String seedVerification) {
        this.seedVerification = seedVerification;
        return this;
    }

    public String getCodeVerification() {
        return codeVerification;
    }

    public UserPayLoad setCodeVerification(String codeVerification) {
        this.codeVerification = codeVerification;
        return this;
    }

    public boolean seedHasBeeVerificate() {
        if (this.getSeedVerification() == null) {
            return false;
        }
        return Objects.equals(this.getSeedVerification(), this.getCodeVerification());
    }

    public boolean seedVerificationIsEmpty() {
        return ObjectUtils.isEmpty(this.getSeedVerification());
    }

    public boolean codeVerificationIsEmpty() {
        return ObjectUtils.isEmpty(this.getCodeVerification());
    }

    public boolean isRenewCode() {
        return renewCode;
    }

    public UserPayLoad setRenewCode(boolean renewCode) {
        this.renewCode = renewCode;
        return this;
    }

    public static final class Builder {
        private String name;
        private String description;
        private String email;
        private String userName;
        private Foto foto;
        private Set<String> nickUsers;
        private String passwd;
        private String fone;

        private boolean odinUser;

        //numero enviado por sms para confirma o numero telefonico
        private String seedVerification;
        //numero que mobilidade receber e que deve ser comparado com o seed
        private String codeVerification;
        private boolean renewCode = false;

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

        public Builder setFoto(final Foto foto) {
            this.foto = foto;
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

        public Builder setFone(String fone) {
            this.fone = fone;
            return this;
        }

        public Builder setOdinUser(boolean odinUser) {
            this.odinUser = odinUser;
            return this;
        }

        public Builder setSeedVerification(String seedVerification) {
            this.seedVerification = seedVerification;
            return this;
        }

        public Builder setCodeVerification(String codeVerification) {
            this.codeVerification = codeVerification;
            return this;
        }

        public Builder setRenewCode(boolean renewCode) {
            this.renewCode = renewCode;
            return this;
        }

        public Builder set(final User user) {
            return this.setName(user.getName())
                    .setDescription(user.getDescription())
                    .setEmail(user.getEmail())
                    .setUserName(user.getUserName())
                    .setFoto(user.getFoto())
                    .setNickUsers(user.getNickUsers())
                    .setOdinUser(user.isOdinUser());
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public UserPayLoad build() {
            return new UserPayLoad(this.name, this.description, this.email, this.userName, this.foto, this.nickUsers, this.passwd, this.fone, this.odinUser, this.seedVerification, this.codeVerification, this.renewCode);
        }
    }
}
