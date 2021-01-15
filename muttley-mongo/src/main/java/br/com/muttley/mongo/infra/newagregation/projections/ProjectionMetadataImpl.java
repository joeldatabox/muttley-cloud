package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                //.filter(it -> !this.propertiesForlookup.contains(it))
                //recuperando item e verificando se o mesmo é um dbref
                //apenas dbref que se faz neessário fazer lookup
                .filter(it -> {
                    //se não contem navegação não precisa de lookup
                    if (it.contains(".")) {
                        //Só vamos fazer lookup se o item atual não for um id e
                        //o item anterior for um dbref
                        //e se o dbref não tiver lookup ainda
                        //para verificar isso vamos navegar pelo indices da string de 0 até o ultimo "."
                        final EntityMetaData fieldAtual = this.entityMetaData.getFieldByName(it);
                        if (fieldAtual != null) {
                            //field atual não é um DBRef e nem um id?
                            if (!fieldAtual.isDBRef() && !fieldAtual.isId()) {
                                final EntityMetaData fieldAnterior = this.entityMetaData.getFieldByName(it.substring(0, it.lastIndexOf(".")));
                                return fieldAnterior.isDBRef() && !this.propertiesForlookup.contains(fieldAnterior.getNameField());
                            }
                        }
                    }
                    return false;
                })
                //adicionado a chave do dbref que chegou ate aqui e criando o lookup necessário
                .map(it -> {
                    final String keyForLookUp;
                    //pegando a referencia do dbref para o cache
                    if (it.contains(".")) {
                        keyForLookUp = it.substring(0, it.lastIndexOf("."));
                    } else {
                        keyForLookUp = it;
                    }
                    this.propertiesForlookup.add(keyForLookUp);
                    return entityMetaData.createProjectFor(keyForLookUp);
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public boolean isDBRef(final String key) {
        final EntityMetaData metaData = this.entityMetaData.getFieldByName(key);
        return metaData != null && metaData.isDBRef();
    }

    @Override
    public boolean isId(String key) {
        final EntityMetaData metaData = this.entityMetaData.getFieldByName(key);
        return metaData != null && metaData.isId();
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
        final Collection<String> result = new LinkedHashSet<>();
        String lastItem = keySplit[0];
        result.add(lastItem);
        for (int i = 1; i < keySplit.length; i++) {
            lastItem = lastItem + "." + keySplit[i];
            //pegando o indice anterior e concatenando com o atual
            result.add(lastItem);
        }
        return result;
    }
}
