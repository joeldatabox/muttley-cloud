package br.com.muttley.security.infra.service;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;

/**
 * @author Joel Rodrigues Moreira on 09/01/18.
 * @project demo
 */
public interface CacheUserAuthenticationService {
    void set(final String token, final JwtUser user);

    JwtUser get(final String token);

    boolean contains(final String token);

    void remove(final JwtToken token);

    /**
     * Faz a atualização de um determinado token preservando as informações já persistidas
     *
     * @param currentToken -> token que será atualizado
     * @param newToken     -> novo token que substituira o antigo
     * @return true -> se de fato ocorreu essa atualização
     */
    boolean refreshToken(final JwtToken currentToken, final JwtToken newToken);
}
