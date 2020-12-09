package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.mongo.infra.newagregation.operators.Operator;
import br.com.muttley.mongo.infra.newagregation.operators.Operator2;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.stream.Stream;

/**
 * @author Joel Rodrigues Moreira on 02/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
public class Criterion2Impl implements Criterion2 {
    @Setter
    @Accessors(chain = true)
    private int order;
    @Setter
    @Accessors(chain = true)
    private int level;
    private final Operator2 operator;
    private final Object value;
    @Setter
    @Accessors(chain = true)
    private String key;//esse campo deve ser deletado posteriormente, o mesmo só serve pra test

    protected Criterion2Impl(Operator2 operator, Object value) {
        this.operator = operator;
        this.value = value;
    }

    /*@Override
    public String toString() {
        return "{" + getOperator() + " : " + getValue() + "}";
    }*/

    @Override
    public String toString() {
        return "Criterion2Impl{" +
                "operator=" + operator.getWildcard() +
                ", key='" + this.replaceAllOperators(key) + '\'' +
                ", value=" + value +
                '}';
    }

    //saporra também deve ser deletada depois dos testes
    private static String replaceAllOperators(final String value) {
        if (!value.contains(".$")) {
            return value;
        }
        String result = value;
        //pegando todos os operadores
        final String[] operators = Stream.of(Operator.values())
                .parallel()
                //pegando a representação em string
                .map(Operator::toString)
                .toArray(String[]::new);
        for (final String widcard : operators) {
            result = result.replace(widcard, "");
        }
        return result;
    }
}
