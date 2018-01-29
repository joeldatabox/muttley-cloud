package br.com.muttley.redis.repository;

import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;

/**
 * @author Joel Rodrigues Moreira on 09/01/18.
 * @project demo
 */
public interface CrudRedisRepository<E, ID extends Serializable> extends CrudRepository<E, ID> {
}
