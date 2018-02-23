package br.com.muttley.domain.service;

import br.com.muttley.model.Model;
import br.com.muttley.model.security.model.User;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public interface ModelService<T extends Model, ID extends Serializable> extends Service<T, ID> {

}
