package br.com.muttley.localcache.services;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;

/**
 * @author Joel Rodrigues Moreira 24/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Seviço responsável por válidar e recuperar usuário atravez de tokens de authenticação
 */
public interface LocalUserAuthenticationService {

    JwtUser getJwtUserFrom(final JwtToken token);

    LocalUserAuthenticationService remove(final JwtToken token);

    /**
     * Faz a atualização de um determinado token preservando as informações já persistidas
     *
     * @param currentToken -> token que será atualizado
     * @param newToken     -> novo token que substituira o antigo
     * @return true -> se de fato ocorreu essa atualização
     */
    void refreshToken(final JwtToken currentToken, final JwtToken newToken);
}
