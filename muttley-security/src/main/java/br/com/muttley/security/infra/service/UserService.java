package br.com.muttley.security.infra.service;

import br.com.muttley.model.security.jwt.JwtUser;
import br.com.muttley.model.security.model.Passwd;
import br.com.muttley.model.security.model.User;
import br.com.muttley.security.infra.response.JwtTokenResponse;
import org.springframework.security.core.Authentication;

import java.util.Collection;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
public interface UserService {
    User save(final User user);

    boolean remove(final User user);

    User update(final User user);

    User updatePasswd(final Passwd user);

    User findByEmail(final String email);

    User findById(final String id);

    Collection<User> findAll();

    Authentication getCurrentAuthentication();

    JwtUser getCurrentJwtUser();

    JwtTokenResponse getCurrentToken();

    User getCurrentUser();
}
