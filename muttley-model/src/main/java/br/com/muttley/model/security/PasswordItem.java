package br.com.muttley.model.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.Date;

/**
 * @author Joel Rodrigues Moreira 12/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
public class PasswordItem {
    private final String password;
    private final Date dtCreate;

    @PersistenceConstructor
    @JsonCreator
    public PasswordItem(@JsonProperty("password") final String password, @JsonProperty("dtCreate") final Date dtCreate) {
        this.password = password;
        this.dtCreate = dtCreate;
    }

    public PasswordItem(final Password password) {
        this(password.getPassword(), password.getLastDatePasswordChanges());
    }
}
