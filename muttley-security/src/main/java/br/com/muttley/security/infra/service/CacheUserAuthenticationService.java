package br.com.muttley.security.infra.service;

import br.com.muttley.model.security.JwtUser;

/**
 * @author Joel Rodrigues Moreira on 09/01/18.
 * @project demo
 */
public interface CacheUserAuthenticationService {
    void set(final String token, final JwtUser user);

    JwtUser get(final String token);

    boolean contains(final String token);
}
