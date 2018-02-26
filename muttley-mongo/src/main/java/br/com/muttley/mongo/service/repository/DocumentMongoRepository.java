package br.com.muttley.mongo.service.repository;

import br.com.muttley.model.Historic;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface DocumentMongoRepository<T, ID extends Serializable> extends MongoRepository<T, ID> {

    /**
     * Busca o primeiro registro qualquer de uma colection
     */
    T findFirst();

    /**
     * Lista registros de uma determinada collection
     *
     * @param queryParams -> parametros para criterios
     */
    List<T> findAll(final Map<String, Object> queryParams);

    /**
     * Conta registros de uma determinada collection
     *
     * @param queryParams -> parametros para criterios
     */
    long count(final Map<String, Object> queryParams);

    /**
     * Verifica se existe um determinado registro no banco de dados
     *
     * @param value -> objeto desejado
     */
    boolean exists(final T value);

    /**
     * Carrega o historico de um determinado registro
     *
     * @param value -> registro a ser carregado
     */
    Historic loadHistoric(final T value);

    /**
     * Carrega o historico de um determinado registro
     *
     * @param id -> id do registro a ser carregado
     */
    Historic loadHistoric(final ID id);
}