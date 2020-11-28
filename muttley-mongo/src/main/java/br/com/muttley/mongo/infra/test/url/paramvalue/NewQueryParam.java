package br.com.muttley.mongo.infra.test.url.paramvalue;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 29/10/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@EqualsAndHashCode(of = {"key", "value"})
public class NewQueryParam {
    private String key;
    private String value;

    public NewQueryParam(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public NewQueryParam(String value) {
        if (value.endsWith("]")) {
            this.key = value.substring(0, value.indexOf("="));
            this.value = value.substring(this.key.length() + 2, value.length() - 1);
        } else {
            final String[] valueSplit = value.split("=");
            this.key = valueSplit[0];
            this.value = valueSplit[1];
        }
    }

    public boolean isArrayValue() {
        return value.startsWith("[") && value.endsWith("]");
    }

    public List<NewQueryParam> valueToArray() {
        return this.extractArrayValue(this.getValue());
    }


    private List<NewQueryParam> extractArrayValue(final String text) {


        if (text == null || text.length() <= 2) {
            throw new MuttleyBadRequestException(null, "$or", "Erro ao executar a consulta").addDetails("$or", "informe algum parametro valido para o operador $or");
        }
        final String otherValue = value.substring(1, value.length() - 1);
        //separando os criterios
        final String[] allCriterions = otherValue.split(";;");
        final List<NewQueryParam> pipelines = new LinkedList<>();
        for (int i = 0; i < allCriterions.length; i++) {
            final String expr[] = allCriterions[i].split(":");
            //evitando que algum animal passe uma string vazia
            if (expr.length > 1) {
                //extraindo os criterios do or
                pipelines.add(new NewQueryParam(expr[0], expr[1]));
            } else {
                //string ta vazia mano
                pipelines.add(new NewQueryParam(expr[0], ""));
            }
        }
        return pipelines;
        //return pipelines;

    }
}
