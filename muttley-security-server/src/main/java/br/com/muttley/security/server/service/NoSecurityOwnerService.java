package br.com.muttley.security.server.service;

import br.com.muttley.model.security.Owner;

/**
 * Essa classe deve ser utilizada apenas para configuração automatica do sistema,
 * A mesma não contem validação de negócio nem mesmo de segurança
 *
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface NoSecurityOwnerService {
    Owner findByName(final String nome);

    Owner save(final Owner owner);
}
