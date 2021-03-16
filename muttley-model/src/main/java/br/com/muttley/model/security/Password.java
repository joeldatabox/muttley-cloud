package br.com.muttley.model.security;

import br.com.muttley.annotations.index.CompoundIndexes;
import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.security.jackson.UserDeserializer;
import br.com.muttley.model.security.jackson.UserSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotNull;
import java.security.SecureRandom;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static io.jsonwebtoken.SignatureAlgorithm.HS512;
import static io.jsonwebtoken.impl.crypto.MacProvider.generateKey;

/**
 * @author Joel Rodrigues Moreira 12/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = "#{documentNameConfig.getNameCollectionPassword()}")
@CompoundIndexes({
        @CompoundIndex(name = "user_index_unique", def = "{'user': 1}", unique = true)
})
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class Password implements br.com.muttley.model.Document {
    @Transient
    @JsonIgnore
    private static final int SALT = 10;
    private static final SecureRandom RANDOM = new SecureRandom(generateKey(HS512).getEncoded());


    @JsonIgnore
    private String id;
    @JsonIgnore
    @NotNull
    @DBRef
    @JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)
    private User user;
    @JsonIgnore
    @NotBlank(message = "Informe uma senha valida!")
    private String password;
    @JsonIgnore
    private List<PasswordItem> oldPasswords;
    @JsonIgnore
    private MetadataDocument metadata;
    @JsonIgnore
    private Historic historic;

    protected Password() {
        this.oldPasswords = new LinkedList<>();
    }

    @PersistenceConstructor
    public Password(final String id, final User user, final String passwd, final List<PasswordItem> oldPasswords, final MetadataDocument metadata, final Historic historic) {
        this.id = id;
        this.user = user;
        this.password = passwd;
        this.oldPasswords = oldPasswords;
        this.metadata = metadata;
        this.historic = historic;
    }

    public Password setPassword(final String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new MuttleySecurityBadRequestException(User.class, "passwd", "Informe uma senha valida!");
        }
        this.password = this.getPasswordEncoder().encode(password);
        return this;
    }

    public Password setPassword(final PasswdPayload passwdPayload) {
        if (!checkPasswd(passwdPayload.getActualPasswd())) {
            throw new MuttleySecurityBadRequestException(User.class, "passwd", "A senha atual informada é invalida!").setStatus(HttpStatus.NOT_ACCEPTABLE);
        }
        //gerando historico das senhas
        this.addOldPassword(new PasswordItem(this))
                .password = this.getPasswordEncoder().encode(passwdPayload.getNewPasswd());
        return this;
    }

    public List<PasswordItem> getOldPasswords() {
        if (this.oldPasswords == null) {
            this.oldPasswords = new LinkedList<>();
        }
        return oldPasswords;
    }

    private Password addOldPassword(final PasswordItem passwordItem) {
        this.oldPasswords.add(0, passwordItem);
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
        return this.getPasswordEncoder().matches(passwd, cryptedPasswd);
    }

    private BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(SALT, RANDOM);
    }

    public Date getLastPasswordResetDate() {
        return this.getHistoric().getDtChange();
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
            return this.setPassword(password.getNewPasswd());
        }

        public Password builder() {
            return new Password()
                    .setPassword(this.password);
        }

        public Builder setUser(final User user) {
            this.user = user;
            return this;
        }
    }
}
