package br.com.muttley.security.server.service;

import br.com.muttley.model.security.AccessPlan;
import org.bson.types.ObjectId;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project agrifocus-cloud
 */
public interface AccessPlanService extends SecurityService<AccessPlan, ObjectId> {
    AccessPlan findByDescription(String descricao);
}
