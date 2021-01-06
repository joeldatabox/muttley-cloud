package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 10/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface ProjectionMetadata {

    /**
     * Converte o parametro para um determinado campo
     */
    Object converteValueFor(final String key, final Object value);

    /**
     * Com base na chave passada, o mesmo retorna as operações necessária de
     * lookup para acessar a propriedade. Caso já tenha sido gerado lookup alguma vez,
     * é retornado um list vazio
     * <p>
     * O mesmo retorna uma cadeia mesmo se tiver navegação de propriedades
     */
    List<AggregationOperation> getLookupOperations(final String key);

    boolean isDBRef(String key);

    /**
     * Informa se já foi gerado lookup para determinada propriedade
     */
    boolean hasBeenGeneratedLookupFor(String key);

    public static class ProjectionMetadataBuilder {
        static ProjectionMetadata build(final EntityMetaData entityMetaData) {
            return new ProjectionMetadataImpl(entityMetaData);
        }
    }
}
