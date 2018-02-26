package br.com.muttley.domain.service;

import br.com.muttley.model.Model;
import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public interface ModelService<T extends Model, ID extends Serializable> extends Service<T, ID> {

}
