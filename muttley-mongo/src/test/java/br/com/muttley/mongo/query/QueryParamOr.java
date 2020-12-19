package br.com.muttley.mongo.query;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.newagregation.paramvalue.NewQueryParam;
import br.com.muttley.mongo.infra.newagregation.projections.Criterion2;
import br.com.muttley.mongo.infra.newagregation.projections.Projection3;
import br.com.muttley.mongo.query.model.Pessoa;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author Joel Rodrigues Moreira on 08/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class QueryParamOr {

    @Test
    public void testExtracSimples() {
        final String primeiroValor = "afd:'5';df:'78'";
        final String segundoValor = "afd.$is:'5';df.$is:'78'";
        final String terceiroValor = "afd.$lt:'5';df.$lte:'78'";

        this.extractParams(primeiroValor).stream().forEach(System.out::println);
        this.extractParams(segundoValor).stream().forEach(System.out::println);
        this.extractParams(terceiroValor).stream().forEach(System.out::println);
    }

    @Test
    public void testExtractComplex() {
        final String primeiroValor = "afd:'5';df:'78';$or:[at:'25';$or:[hh:'855']]";
        System.out.println("item processado -> " + primeiroValor);
        this.extractParams(primeiroValor).stream().forEach(System.out::println);

        Projection3.ProjectionBuilder.from(EntityMetaData.of(Pessoa.class), URLParaTest.getQueryParams("www.asdf.com?$or=["+primeiroValor+"]"));
    }

    public LinkedList<Criterion2> extractParams(final String params) {
        final LinkedList<Criterion2> result = new LinkedList<>();
        //talvez tenhamos subarraye para isso precisamos dessa varivel para contro
        final String operador;
        //verificando se precisaremo fazer recusão para arrays internos
        if (params.contains("[") || params.contains("]")) {
            final int totalIniChave = StringUtils.countOccurrencesOf(params, "[");
            final int totalAtribuicaoChave = StringUtils.countOccurrencesOf(params, ":[");
            final int totalEndChave = StringUtils.countOccurrencesOf(params, "]");

            //verificando se tem a quantidade de abertura e fechamento de chaves
            if (!(totalIniChave == totalEndChave && totalIniChave == totalAtribuicaoChave)) {

                throw new MuttleyBadRequestException(this.getClass(), null, "a expressão de consulta é inválida, por favor verifique")
                        .addDetails("expression", params)
                        .addDetails("obs", totalIniChave > totalEndChave ? "está faltando fechar chavez" : totalIniChave < totalEndChave ? "está faltando abrir chaves" : "está faltando atribuição de arra com ':'");
            }
            //nesse momento teremos que pegar subblocos de informações
            //para percorrer recursivamente
            //1º [ e o ] e chamamos a cascata de informação com recursividade
            final int startArray = params.indexOf("[");
            final String paramSemArray = params.substring(0, startArray);
            //atribuindo o operador
            operador = paramSemArray.substring(paramSemArray.lastIndexOf("$"));
            System.out.println("Operador encontrado " + operador);
            System.out.println("Parametros extraido " + params.substring(startArray + 1, params.lastIndexOf("]")));
            result.addAll(this.extractParams(params.substring(startArray + 1, params.lastIndexOf("]"))));

        } else {
            //se chegou até aqui é sinal que não tem subarrays
            operador = null;
        }

        //quebrando a string em array de chave:valor
        result.addAll(
                Stream.of(operador == null ? params.split(";") : params.substring(0, params.indexOf(operador)).split(";")).parallel()
                        //tirando itens vazios
                        .filter(it -> !StringUtils.isEmpty(it))
                        //transformando chave:valor em objeto
                        .map(it -> {
                            //separando chave:valor
                            final String[] keyParam = it.split(":");
                            return new NewQueryParam(keyParam[0], keyParam[1].replace("'", ""));
                        })
                        .map(it -> Criterion2.Criterion2Builder.from(it))
                        .collect(toList())
        );

        return result;
    }
}
