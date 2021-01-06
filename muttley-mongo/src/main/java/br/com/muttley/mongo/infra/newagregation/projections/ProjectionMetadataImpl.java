package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 10/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ProjectionMetadataImpl implements ProjectionMetadata {
    /**
     * Iremos armazenar aqui toda a cadeia de propriedades que são dbref e que precisa de lookup.
     * Quando o lookup for gerado a alteraremos o status
     */
    private final Set<String> propertiesForlookup;
    private final EntityMetaData entityMetaData;

    protected ProjectionMetadataImpl(final EntityMetaData entityMetaData) {
        this.propertiesForlookup = new HashSet<>();
        this.entityMetaData = entityMetaData;
    }

    @Override
    public Object converteValueFor(String key, Object value) {
        final EntityMetaData metaData = this.entityMetaData.getFieldByName(key);
        return metaData != null ? metaData.converteValue(value) : value;
    }

    @Override
    public List<AggregationOperation> getLookupOperations(String key) {
        return this.createChainkeys(key)//gerando uma cadeia de chaves para fazer as navegaçõoes
                //transformando em um fluxo
                .stream()
                //se a propriedade já existe quer dizer que já foi feito outro lookup
                .filter(it -> !this.propertiesForlookup.contains(it))
                //recuperando item e verificando se o mesmo é um dbref
                //apenas dbref que se faz neessário fazer lookup
                .filter(it -> {
                    final EntityMetaData field = this.entityMetaData.getFieldByName(it);
                    return field != null && field.isDBRef();
                })
                //adicionado a chave que chegou ate aqui e criando o lookup necessário
                .map(it -> {
                    this.propertiesForlookup.add(it);
                    return entityMetaData.createProjectFor(it);
                })
                .reduce((acc, others) -> {
                    acc.addAll(others);
                    return acc;
                }).orElse(new LinkedList<>());
    }

    @Override
    public boolean isDBRef(String key) {
        final EntityMetaData metaData = this.entityMetaData.getFieldByName(key);
        return metaData != null && metaData.isDBRef();
    }

    @Override
    public boolean hasBeenGeneratedLookupFor(String key) {
        return this.propertiesForlookup.contains(key);
    }

    /**
     * Cria uma cadeia de chaves baseada na chave passada como parametro,
     * ex.: carro.roda.pneu
     * saida:[carro, carro.roda, carro.roda.pneu]
     */
    private Collection<String> createChainkeys(final String key) {
        if (!key.contains(".")) {
            return Arrays.asList(key);
        }
        final String[] keySplit = key.split("\\.");
        final Set<String> result = new HashSet<>();
        result.add(keySplit[0]);
        for (int i = 1; i < keySplit.length; i++) {
            //pegando o indice anterior e concatenando com o atual
            result.add(keySplit[i - 1] + "." + keySplit[i]);
        }
        return result;
    }
}
