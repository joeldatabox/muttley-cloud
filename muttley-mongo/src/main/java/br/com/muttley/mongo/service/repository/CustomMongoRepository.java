package br.com.muttley.mongo.service.repository;

import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.security.Owner;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CustomMongoRepository<T> extends DocumentMongoRepository<T> {
    /**
     * Sava um registro registro simples
     *
     * @param owner -> dono do registro
     * @param value -> objeto a ser salvo
     */
    T save(final Owner owner, final T value);

    /**
     * Busca um simples registro
     *
     * @param owner -> dono do registro
     * @param id    -> id do objeto desejado
     */
    T findOne(final Owner owner, final String id);

    /**
     * Busca vários registros simples
     *
     * @param owner -> dono do registro
     * @param ids   -> ids dos registros desejado
     */
    Set<T> findMulti(final Owner owner, final String ids[]);

    /**
     * Busca o primeiro registro qualquer de uma colection
     *
     * @param owner -> dono do registro
     */
    T findFirst(final Owner owner);

    /**
     * Deleta um registro usando como critério o ID
     *
     * @param owner -> dono do registro
     * @param id    -> id do objeto desejado
     */
    void delete(final Owner owner, final String id);

    /**
     * Deleta um registro
     *
     * @param owner -> dono do registro
     * @param value -> objeto desejado
     */
    void delete(final Owner owner, final T value);

    /**
     * Lista registros de uma determinada collection
     *
     * @param owner       -> dono do registro
     * @param queryParams -> parametros para criterios
     */
    List<T> findAll(final Owner owner, final Map<String, Object> queryParams);

    /**
     * Conta registros de uma determinada collection
     *
     * @param owner       -> dono do registro
     * @param queryParams -> parametros para criterios
     */
    long count(final Owner owner, final Map<String, Object> queryParams);

    /**
     * Verifica se existe um determinado registro no banco de dados
     *
     * @param owner -> dono do registro
     * @param value -> objeto desejado
     */
    boolean exists(final Owner owner, final T value);

    /**
     * Verifica se existe um determinado registro no banco de dados
     *
     * @param owner -> dono do registro
     * @param id    -> id do objeto desejado
     */
    boolean exists(final Owner owner, final String id);

    /**
     * Verifica se existe um determinado registro no banco de dados
     *
     * @param owner  -> dono do registro
     * @param filter -> Map com criterios de filtro
     */
    boolean exists(final Owner owner, final Map<String, Object> filter);

    /**
     * Verifica se existe um determinado registro no banco de dados
     *
     * @param owner -> dono do registro
     *              * @param filter -> vetor com criterios de filtro. Deve sempre ser passar na seguinte expressão [campo, critério]
     */
    boolean exists(final Owner owner, final Object... filter);

    /**
     * Carrega o historico de um determinado registro
     *
     * @param owner -> dono do registro
     * @param value -> registro a ser carregado
     */
    Historic loadHistoric(final Owner owner, final T value);

    /**
     * Carrega o metadata de um determinado registro
     *
     * @param owner -> dono do registro
     * @param value -> registro a ser carregado
     */
    MetadataDocument loadMetaData(final Owner owner, final T value);

    /**
     * Carrega o historico de um determinado registro
     *
     * @param owner -> dono do registro
     * @param id    -> id do registro a ser carregado
     */
    Historic loadHistoric(final Owner owner, final String id);

    /**
     * Carrega o metadata de um determinado registro
     *
     * @param owner -> dono do registro
     * @param id    -> id do registro a ser carregado
     */
    MetadataDocument loadMetaData(final Owner owner, final String id);
}
