package br.com.muttley.security.server.service;

import br.com.muttley.model.security.PasswdPayload;
import br.com.muttley.model.security.Password;
import br.com.muttley.model.security.User;

/**
 * @author Joel Rodrigues Moreira 12/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

public interface PasswordService<T extends Password> {
    Password findByUserId(final String userId);

    void save(final User user, final T password);

    void createPasswordFor(final User user, final String password);

    void createPasswordFor(final User user, final PasswdPayload password);

    void update(final PasswdPayload password);
}
