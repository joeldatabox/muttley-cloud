package br.com.muttley.model.security;

import br.com.muttley.annotations.index.CompoundIndexes;
import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.security.jackson.UserDeserializer;
import br.com.muttley.model.security.jackson.UserSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.security.SecureRandom;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static br.com.muttley.model.security.Password.BuilderPasswordEncoder.getPasswordEncoder;
import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.impl.crypto.MacProvider.generateKey;

/**
 * @author Joel Rodrigues Moreira 12/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = "#{documentNameConfig.getNameCollectionPassword()}")
@CompoundIndexes({
        @CompoundIndex(name = "user_index_unique", def = "{'user': 1}")
})
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class Password implements br.com.muttley.model.Document {
    /*@Transient
    @JsonIgnore
    private static final int SALT = 10;
    private static final SecureRandom RANDOM = new SecureRandom(generateKey(HS512).getEncoded());*/


    @JsonIgnore
    private String id;
    @JsonIgnore
    @NotNull
    @DBRef
    @JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)
    private User user;
    @NotBlank(message = "Informe uma senha valida!")
    private String password;
    @NotNull(message = "Informe uma senha valida!")
    private Date lastDatePasswordChanges;
    @JsonIgnore
    private List<PasswordItem> oldPasswords;
    @JsonIgnore
    private MetadataDocument metadata;

    protected Password() {
        this.oldPasswords = new LinkedList<>();
    }

    @PersistenceConstructor
    @JsonCreator
    public Password(
            @JsonProperty("id") final String id,
            @JsonProperty("user") final User user,
            @JsonProperty("password") final String password,
            @JsonProperty("lastDatePasswordChanges") Date lastDatePasswordChanges,
            @JsonProperty("oldPasswords") final List<PasswordItem> oldPasswords,
            @JsonProperty("metadata") final MetadataDocument metadata) {
        this.id = id;
        this.user = user;
        this.password = password;
        this.lastDatePasswordChanges = lastDatePasswordChanges;
        this.oldPasswords = oldPasswords;
        this.metadata = metadata;
    }

    public Password setPassword(final String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new MuttleySecurityBadRequestException(User.class, "passwd", "Informe uma senha valida!");
        }
        if (!StringUtils.isEmpty(this.password)) {
            //gerando historico das senhas
            this.addOldPassword(new PasswordItem(this));
        }
        this.password = getPasswordEncoder().encode(password);
        this.setLastDatePasswordChanges(new Date());
        return this;
    }

    public Password setPassword(final PasswdPayload passwdPayload) {
        if (!checkPasswd(passwdPayload.getActualPassword())) {
            throw new MuttleySecurityBadRequestException(User.class, "passwd", "A senha atual informada é invalida!").setStatus(HttpStatus.NOT_ACCEPTABLE);
        }
        if (!StringUtils.isEmpty(this.password)) {
            //gerando historico das senhas
            this.addOldPassword(new PasswordItem(this));
        }
        this.password = getPasswordEncoder().encode(passwdPayload.getNewPassword());
        this.setLastDatePasswordChanges(new Date());
        return this;
    }

    public List<PasswordItem> getOldPasswords() {
        if (this.oldPasswords == null) {
            this.oldPasswords = new LinkedList<>();
        }
        return oldPasswords;
    }

    private Password addOldPassword(final PasswordItem passwordItem) {
        this.getOldPasswords().add(0, passwordItem);
        if (this.oldPasswords.size() > 5) {
            this.setOldPasswords(this.oldPasswords.subList(0, 5));
        }
        return this;
    }

    public Password setPasswd(final Password password) {
        this.password = password.password;
        return this;
    }

    public boolean checkPasswd(final String passwd) {
        return checkPasswd(passwd, this.password);
    }

    @JsonIgnore
    public boolean isValidPasswd() {
        return this.password != null && !this.password.isEmpty();
    }

    public boolean checkPasswd(final String passwd, String cryptedPasswd) {
        return getPasswordEncoder().matches(passwd, cryptedPasswd);
    }

    public static class Builder {
        private User user;
        private String password;

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder setPassword(final String password) {
            this.password = password;
            return this;
        }

        public Builder setPassword(final PasswdPayload password) {
            return this.setPassword(password.getNewPassword());
        }

        public Password builder() {
            return new Password()
                    .setUser(this.user)
                    .setLastDatePasswordChanges(new Date())
                    .setPassword(this.password);
        }

        public Builder setUser(final User user) {
            this.user = user;
            return this;
        }
    }

    public static class BuilderPasswordEncoder {
        private static final int SALT = 10;
        private static final SecureRandom RANDOM = new SecureRandom(generateKey(HS512).getEncoded());

        public static BCryptPasswordEncoder getPasswordEncoder() {
            return new BCryptPasswordEncoder(SALT, RANDOM);
        }
    }
}
