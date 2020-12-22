package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.mongo.infra.newagregation.operators.Operator2;
import br.com.muttley.mongo.infra.newagregation.paramvalue.NewQueryParam;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static br.com.muttley.mongo.infra.newagregation.operators.Operator2.OR;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 10/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface Criterion3 {
    Operator2 getOperator();

    String getKey();

    Object getValue();

    List<Criterion3> getSubcriterions();

    public static class CriterionBuilder {

        public static Criterion3 from(final ProjectionMetadata metadata, final NewQueryParam param) {
            final Operator2 operator2 = Operator2.of(param.getKey());
            final Criterion3Impl result;
            if (OR.equals(operator2)) {
                result = new Criterion3Impl(metadata, operator2, null, null, extractCriterionsArray(metadata, param.getValue()));//precisa extrair os subitens aqui
            } else {
                result = new Criterion3Impl(metadata, operator2, param.getKey(), param.getValue(), null);
                //result = null;
            }
            return result;
        }

        /**
         * Extrai toda a cadeia de criterio contido em expressões para arrays
         */
        private static List<Criterion3> extractCriterionsArray(final ProjectionMetadata metadata, final String params) {
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
                final Operator2 subOperator = Operator2.of(params.substring(startSubOperator, endSubOperator));

                //adicionando criterio extraido de subArray;
                criterios.add(0, new Criterion3Impl(metadata, subOperator, itensSubArray));

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
        private static List<Criterion3> extractCriterions(final ProjectionMetadata metadata, final String params) {
            return new LinkedList<>(
                    //quebrando os itens
                    Stream.of(params.split(";"))
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
    }
}
