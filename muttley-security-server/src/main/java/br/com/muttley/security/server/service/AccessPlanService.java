package br.com.muttley.security.server.service;

import br.com.muttley.model.security.AccessPlan;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface AccessPlanService extends SecurityService<AccessPlan> {
    AccessPlan findByDescription(String descricao);
}
