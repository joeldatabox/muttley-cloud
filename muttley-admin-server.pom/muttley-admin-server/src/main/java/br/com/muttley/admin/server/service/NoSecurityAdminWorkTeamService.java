package br.com.muttley.admin.server.service;

import br.com.muttley.model.admin.AdminWorkTeam;

/**
 * Essa classe deve ser utilizada apenas para configuração automatica do sistema,
 * A mesma não contem validação de negócio nem mesmo de segurança
 *
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface NoSecurityAdminWorkTeamService {
    AdminWorkTeam save(final AdminWorkTeam workTeam);
}
