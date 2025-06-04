package br.com.muttley.security.server.service;


import br.com.muttley.domain.service.Service;
import br.com.muttley.model.parametrizacao.ParametrizacaoToggle;
import br.com.muttley.model.parametrizacao.Parametro;
import br.com.muttley.model.security.User;

import java.util.Optional;

/**
 * @author Carolina Cedro on 14/05/25.
 * e-mail: <a href="mailto:ana.carolina@maxxsoft.com.br">ana.carolina@maxxsoft.com.br</a>
 * @project agrifocus-cloud
 */

public interface ToggleClienteService extends Service<ParametrizacaoToggle> {

    /**
     * Busca uma parametrização pelo ID da empresa
     */
    Optional<ParametrizacaoToggle> buscarPorUser(User userView);

    /**
     * Busca o valor de uma chave específica de uma empresa
     */
    Optional<String> getValorParametro(User userView, String chave);

    /**
     * Cria ou atualiza um parâmetro de uma empresa
     */
    ParametrizacaoToggle salvarOuAtualizarParametro(User userView, Parametro parametro);

}
