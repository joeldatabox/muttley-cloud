package br.com.muttley.security.server.service;


import br.com.muttley.domain.service.ModelSyncService;
import br.com.muttley.model.parametrizacao.ParametrizacaoToggle;
import br.com.muttley.model.parametrizacao.Parametro;
import br.com.muttley.model.security.User;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

/**
 * @author Carolina Cedro on 14/05/25.
 * e-mail: <a href="mailto:ana.carolina@maxxsoft.com.br">ana.carolina@maxxsoft.com.br</a>
 * @project agrifocus-cloud
 */

public interface ToggleClienteService extends ModelSyncService<ParametrizacaoToggle> {

    /**
     * Busca uma parametrização pelo ID da empresa
     */
    @PreAuthorize(
            "this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles())" +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true"
    )
    Optional<ParametrizacaoToggle> buscarPorUser(User userView);

    /**
     * Busca o valor de uma chave específica de uma empresa
     */
    @PreAuthorize(
            "this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles())" +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true"
    )
    Optional<String> getValorParametro(User userView, String chave);

    /**
     * Cria ou atualiza um parâmetro de uma empresa
     */
    @PreAuthorize(
            "this.isCheckRole()? " +
                    "(" +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).ROLE_OWNER.toString(), " +
                    "       T(br.com.muttley.model.security.Role).ROLE_ROOT.toString()" +
                    "   ) " +
                    "or " +
                    "   hasAnyRole(" +
                    "       T(br.com.muttley.model.security.Role).toPatternRole('read', this.getBasicRoles())" +
                    "   )" +
                    "or (" +
                    "   @userAgent.isMobile()? " +
                    "       ( " +
                    "           hasAnyRole( " +
                    "               T(br.com.muttley.model.security.Role).toPatternRole('read', 'MOBILE_' + this.getBasicRoles()) " +
                    "           ) " +
                    "       ):false " +
                    "   )" +
                    "): " +
                    "   true"
    )
    ParametrizacaoToggle salvarOuAtualizarParametro(User userView, Parametro parametro);

}
