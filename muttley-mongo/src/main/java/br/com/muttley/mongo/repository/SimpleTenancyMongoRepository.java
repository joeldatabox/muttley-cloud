package br.com.muttley.mongo.repository;

import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.mongo.infra.newagregation.paramvalue.QueryParam;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Map;
import java.util.Set;

@NoRepositoryBean
public interface SimpleTenancyMongoRepository<T> extends MongoRepository<T, String> {

    /**
     * Verifica se a collection está vazia
     */
    boolean isEmpty();

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
     * @param params -> parametros da para criterios
     */
    List<T> findAll(final List<QueryParam> params);

    /**
     * Conta registros de uma determinada collection
     *
     * @param params -> parametros da para criterios
     */
    long count(final List<QueryParam> params);

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
     * Carrega o historico de um determinado registro
     *
     * @param value -> registro a ser carregado
     */
    Historic loadHistoric(final T value);

    /**
     * Carrega o metadata de um determinado registro
     *
     * @param id -> id do registro a ser carregado
     */
    MetadataDocument loadMetadata(final String id);

    /**
     * Carrega o historico de um determinado registro
     *
     * @param id -> id do registro a ser carregado
     */
    Historic loadHistoric(final String id);
}
