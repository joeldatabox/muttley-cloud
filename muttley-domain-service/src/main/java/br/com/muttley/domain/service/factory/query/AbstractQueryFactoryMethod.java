package br.com.muttley.domain.service.factory.query;

/**
 * @author Joel Rodrigues Moreira 17/08/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Interface básica para armazenar a implementação básica uma fábrica de
 * query de um determinado metodo
 */
public abstract class AbstractQueryFactoryMethod {
    protected abstract AbstractQueryFactory factory();
}
