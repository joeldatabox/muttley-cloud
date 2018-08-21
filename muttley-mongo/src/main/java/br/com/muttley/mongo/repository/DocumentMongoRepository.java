package br.com.muttley.mongo.repository;

import br.com.muttley.model.Historic;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface DocumentMongoRepository<T> extends MongoRepository<T, String> {

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
    Historic loadHistoric(final String id);
}