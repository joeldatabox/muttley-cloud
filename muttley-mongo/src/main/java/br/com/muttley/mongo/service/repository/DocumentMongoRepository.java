package br.com.muttley.mongo.service.repository;

import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface DocumentMongoRepository<T> extends MongoRepository<T, String> {

    /**
     * Busca vários registros simples
     *
     * @param ids -> ids dos registros desejado
     */
    Set<T> findMulti(final String ids[]);

    /**
     * Busca o primeiro registro qualquer de uma colection
     */
    T findFirst();

    /**
     * Lista registros de uma determinada collection
     *
     * @param queryParams -> parametros para criterios
     */
    List<T> findAll(final Map<String, String> queryParams);

    /**
     * Conta registros de uma determinada collection
     *
     * @param queryParams -> parametros para criterios
     */
    long count(final Map<String, String> queryParams);

    /**
     * Verifica se existe um determinado registro no banco de dados
     *
     * @param value -> objeto desejado
     */
    boolean exists(final T value);

    /**
     * Verifica se existe um determinado registro no banco de dados
     *
     * @param filter -> Map com criterios de filtro
     */
    boolean exists(final Map<String, Object> filter);

    /**
     * Verifica se existe um determinado registro no banco de dados
     *
     * @param filter -> vetor com criterios de filtro. Deve sempre ser passar na seguinte expressão [campo, critério]
     */
    boolean exists(final Object... filter);

    /**
     * Carrega o metadata de um determinado registro
     *
     * @param value -> registro a ser carregado
     */
    MetadataDocument loadMetadata(final T value);

    /**
     * Carrega o metadata de um determinado registro
     *
     * @param id -> id do registro a ser carregado
     */
    MetadataDocument loadMetadata(final String id);
}
