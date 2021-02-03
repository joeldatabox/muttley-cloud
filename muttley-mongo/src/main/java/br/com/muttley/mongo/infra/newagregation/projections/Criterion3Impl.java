package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.mongo.infra.newagregation.operators.Operator3;
import br.com.muttley.mongo.infra.newagregation.paramvalue.NewQueryParam;
import lombok.Getter;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static br.com.muttley.mongo.infra.newagregation.operators.Operator3.values;
import static br.com.muttley.mongo.infra.newagregation.projections.Criterion3.CriterionBuilder.from;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 10/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
public class Criterion3Impl implements Criterion3 {
    private Operator3 operator;
    private String key;
    private Object value;
    private List<Criterion3> subcriterions;
    private final ProjectionMetadata metadata;


    protected Criterion3Impl(final ProjectionMetadata metadata, final Operator3 operator, final String key, final Object value) {
        this.metadata = metadata;
        this.operator = operator;
        this.key = this.replaceAllOperators(key);
        this.value = value;
        this.subcriterions = new LinkedList<>();
    }

    protected Criterion3Impl addSubcriterions(final Criterion3 criterion3) {
        if (criterion3 != null) {
            this.subcriterions.add(criterion3);
        }
        return this;
    }

    protected Criterion3Impl addSubcriterions(final Collection<Criterion3> criterion3) {
        if (!CollectionUtils.isEmpty(criterion3)) {
            criterion3.forEach(it -> this.addSubcriterions(it));
        }
        return this;
    }

    protected Criterion3Impl addSubcriterionsArray(final String params) {
        return this.addSubcriterions(this.extractCriterionsArray(this.getMetadata(), params));
    }


    @Override
    public List<AggregationOperation> extractAgregations() {
        final List<AggregationOperation> operations = new LinkedList<>(this.metadata.getLookupOperations(this.key));
        operations.addAll(this.operator.extractAggregations(this.metadata, this.key, this.value));
        return operations;
    }

    @Override
    public List<Criteria> extractCriteria() {
        if (this.operator.isTypeArray()) {
            return this.operator.extractCriteriaArray(this.metadata, this.subcriterions);
/*
            return new LinkedList<>(
                    asList(new Criteria()
                            .orOperator(
                                    this.subcriterions
                                            .stream()
                                            .map(it -> it.extractCriteria())
                                            .flatMap(Collection::stream)
                                            .toArray(Criteria[]::new)
                            )
                    )
            );*/
        } else {
            final String key;
            //achustando caso a key seja um id e nao use "$" para referenciar
            if (this.metadata.isId(this.key)) {
                key = this.key.substring(0, this.key.lastIndexOf(".")) + ".$" + this.key.substring(this.key.lastIndexOf(".") + 1).replace("$", "");
            } else {
                key = this.key;
            }
            return this.operator.extractCriteria(this.metadata, key, this.value);
        }
    }

    /**
     * Extrai toda a cadeia de criterio contido em expressões para arrays
     */
    protected List<Criterion3> extractCriterionsArray(final ProjectionMetadata metadata, final String params) {
        List<Criterion3> criterios = new LinkedList<>();

        //armazena todos os parametros sem subArrays
        final String paramsWithoutSubarray;
        //verificando se precisaremos fazer recursão para arrays internos
        if (params.contains("[") || params.contains("]")) {
            final int totalIniChave = StringUtils.countOccurrencesOf(params, "[");
            final int totalAtribuicaoChave = StringUtils.countOccurrencesOf(params, ":[");
            final int totalEndChave = StringUtils.countOccurrencesOf(params, "]");
            //verificando se tem a quantidade de abertura e fechamento de chaves
            if (!(totalIniChave == totalEndChave && totalIniChave == totalAtribuicaoChave)) {

                throw new MuttleyBadRequestException(null, null, "a expressão de consulta é inválida, por favor verifique")
                        .addDetails("expression", params)
                        .addDetails("obs", totalIniChave > totalEndChave ? "está faltando fechar chavez" : totalIniChave < totalEndChave ? "está faltando abrir chaves" : "está faltando atribuição de arra com ':'");
            }
            //nesse momento teremos que pegar subblocos de informações
            //para percorrer recursivamente subarrays contidos nos parametros

            //pegando a primeira ocorrencia de abertura chave de um subarray
            final int startSubArray = params.indexOf("[") + 1;
            //pegando a ultima ocorrencia de abertura chave de um subarray
            final int endSubArray = params.lastIndexOf("]");

            //extraindo os criterios do subArray
            final List<Criterion3> itensSubArray = extractCriterionsArray(metadata, params.substring(startSubArray, endSubArray));

            //extraindo o operador do subArray
            //onde começa o operador do subArray
            final int startSubOperator = params.substring(0, startSubArray - 2).lastIndexOf("$");
            //onde termina o operador do subArray
            final int endSubOperator = params.substring(0, startSubArray).lastIndexOf(":[");
            //operador extraido
            final Operator3 subOperator = Operator3.from(params.substring(startSubOperator, endSubOperator));

            //adicionando criterio extraido de subArray;
            criterios.add(0, new Criterion3Impl(metadata, subOperator, null, null).addSubcriterions(itensSubArray));

            //se chegou até aqui é sinal que já consumimo todos os subArrays
            //e que podemos seguir consumindo itens individuais
            //coletando itens individuas para serem extraidos
            paramsWithoutSubarray = (params.substring(0, startSubOperator) + params.substring(endSubArray + 1)).replaceAll(";;", ";");
        } else {
            //se chegou até aqui é sinal que não tem subarray,
            //logo os proprios params devem ser consumidos
            paramsWithoutSubarray = params;
        }
        criterios.addAll(0, extractCriterions(metadata, paramsWithoutSubarray));
        return criterios;
    }

    /**
     * Extrai toda a cadeia de criterio contido em expressões para arrays
     */
    protected List<Criterion3> extractCriterions(final ProjectionMetadata metadata, final String params) {
        return new LinkedList<>(
                //quebrando os itens
                of(params.split(";"))
                        //removendo itens vazios
                        .filter(it -> !isEmpty(it))
                        //transformando chave:valor em objeto
                        .map(it -> {
                            //separando chave:valor
                            final String[] keyParam = it.split(":");
                            return new NewQueryParam(keyParam[0], keyParam[1].replace("'", ""));
                        })
                        .map(it -> from(metadata, it))
                        .collect(toList())
        );
    }

    /**
     * Remove qualquer operador presente em uma string
     * por exemplo:
     *
     * @param value => "pessoa.nome.$is"
     * @return "pessoa.nome"
     */
    private final String replaceAllOperators(final String value) {
        if (!StringUtils.isEmpty(value)) {
            //lista de widcards
            final String[] operators = of(values())
                    .parallel()
                    .map(Operator3::toString)
                    .toArray(String[]::new);

            String result = value;

            //removendo tudo
            for (final String widcard : operators) {
                result = result.replace(widcard, "");
            }

            return result;
        }
        return value;
    }
}
