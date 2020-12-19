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
                from(metadata, param.getValue());
                result = new Criterion3Impl(metadata, operator2, null, null, null);//precisa extrair os subitens aqui
            } else {
                result = new Criterion3Impl(metadata, operator2, param.getKey(), param.getValue(), null);
                //result = null;
            }
            return result;
        }

        private static Criterion3 extractSimpleCriterion(final ProjectionMetadata metadata, final NewQueryParam param) {
            final Operator2 operator2 = Operator2.of(param.getKey());
            final Criterion3Impl result;
            if (operator2.isTypeArray()) {

            } else {
                //result = new Criterion3Impl(metadata, param.getKey())
            }
            return null;
        }


        /**
         * Extrai toda a cadeia de criterio contido em expressões para arrays
         */
        private static List<Criterion3> from(final ProjectionMetadata metadata, final String paramsOfArray) {
            final List<Criterion3> result = new LinkedList<>();
            //talvez tenhamos subarraye para isso precisamos dessa varivel para controle
            final Operator2 operator;
            //verificando se precisaremo fazer recusão para arrays internos
            if (paramsOfArray.contains("[") || paramsOfArray.contains("]")) {
                final int totalIniChave = StringUtils.countOccurrencesOf(paramsOfArray, "[");
                final int totalAtribuicaoChave = StringUtils.countOccurrencesOf(paramsOfArray, ":[");
                final int totalEndChave = StringUtils.countOccurrencesOf(paramsOfArray, "]");

                //verificando se tem a quantidade de abertura e fechamento de chaves
                if (!(totalIniChave == totalEndChave && totalIniChave == totalAtribuicaoChave)) {

                    throw new MuttleyBadRequestException(null, null, "a expressão de consulta é inválida, por favor verifique")
                            .addDetails("expression", paramsOfArray)
                            .addDetails("obs", totalIniChave > totalEndChave ? "está faltando fechar chavez" : totalIniChave < totalEndChave ? "está faltando abrir chaves" : "está faltando atribuição de arra com ':'");
                }
                //nesse momento teremos que pegar subblocos de informações
                //para percorrer recursivamente
                //1º [ e o ] e chamamos a cascata de informação com recursividade
                final int startArray = paramsOfArray.indexOf("[");
                final String paramSemArray = paramsOfArray.substring(0, startArray);

                //atribuindo o operador
                operator = Operator2.of(paramSemArray.substring(paramSemArray.lastIndexOf("$")));
                //estamo trabalhando com tipo de array?
                if (operator.isTypeArray()) {
                    //extraindo operações de subarrayencontrado
                    final List<Criterion3> subCriterions = CriterionBuilder.from(metadata, paramsOfArray.substring(startArray + 1, paramsOfArray.lastIndexOf("]")));

                    result.add(new Criterion3Impl(metadata, operator, subCriterions));
                } else {

                }
                //atribuindo o operador
                /*operador = paramSemArray.substring(paramSemArray.lastIndexOf("$"));
                System.out.println("Operador encontrado " + operador);
                System.out.println("Parametros extraido " + params.substring(startArray + 1, params.lastIndexOf("]")));
                result.addAll(this.extractParams(params.substring(startArray + 1, params.lastIndexOf("]"))));*/

            } else {
                //se chegou até aqui é sinal que não tem subarrays
                operator = null;
            }

            //quebrando a string em array de chave:valor
            result.addAll(
                    Stream.of(operator == null ? paramsOfArray.split(";") : paramsOfArray.substring(0, paramsOfArray.indexOf(operator.getWildcard())).split(";")).parallel()
                            //tirando itens vazios
                            .filter(it -> !StringUtils.isEmpty(it))
                            //transformando chave:valor em objeto
                            .map(it -> {
                                //separando chave:valor
                                final String[] keyParam = it.split(":");
                                return new NewQueryParam(keyParam[0], keyParam[1].replace("'", ""));
                            })
                            .map(it -> Criterion3.CriterionBuilder.from(metadata, it))
                            .collect(toList()));


            return result;
        }
    }
}
