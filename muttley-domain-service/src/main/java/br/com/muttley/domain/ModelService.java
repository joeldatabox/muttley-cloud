package br.com.muttley.domain;

import br.com.muttley.model.MultiTenancyModel;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public interface ModelService<T extends MultiTenancyModel> extends Service<T> {

}
