package br.com.muttley.model.security;

import lombok.Getter;

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

    public PasswordItem(final String password, final Date dtCreate) {
        this.password = password;
        this.dtCreate = dtCreate;
    }

    public PasswordItem(final Password password) {
        this(password.getPassword(), password.getLastDatePasswordChanges());
    }
}
