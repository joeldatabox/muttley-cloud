package br.com.muttley.mongo.query;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import br.com.muttley.mongo.infra.newagregation.projections.Projection;
import br.com.muttley.mongo.infra.newagregation.projections.Projection2;
import br.com.muttley.mongo.infra.newagregation.projections.Projection3;
import br.com.muttley.mongo.query.model.Pessoa;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.DEFAULT_CONTEXT;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

/**
 * @author Joel Rodrigues Moreira on 29/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project agrifocus-cloud
 */
/*@RunWith(SpringRunner.class)
@SpringBootTest*/
public class QueryParamTest {

    public static Map<String, List<String>> getQueryParams(String url) {
        try {
            Map<String, List<String>> params = new LinkedHashMap<>();
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1) {
                String query = urlParts[1];
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    String key = URLDecoder.decode(pair[0], "UTF-8");
                    String value = "";
                    if (pair.length > 1) {
                        value = URLDecoder.decode(pair[1], "UTF-8");
                    }

                    List<String> values = params.get(key);
                    if (values == null) {
                        values = new LinkedList<>();
                        params.put(key, values);
                    }
                    values.add(value);
                }
            }

            return params;
        } catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }
    }

    @Test
    public void test() throws MalformedURLException {
     /*   final URL url = new URL("http://www.test.com?nome.$contains=5&nome.$contains=asdf");
        //System.out.println(splitQuery(url));
        System.out.println(getQueryParams(url.toString()));
        getQueryParams(url.toString()).entrySet()
                .stream()
                .forEach(entr -> {
                    System.out.println("key " + entr.getKey());
                    entr.getValue().stream().forEach(it -> System.out.println("value = " + it));
                });*/
        //Stream.of(Operator.values()).map(Operator::getRegularExpression).forEach(System.out::println);
        /*System.out.println(Stream.of(Operator.values()).map(Operator::getRegularExpression).collect(Collectors.joining("|")));
        System.out.println(QueryBuilder.replaceAllOperators("tetes.$orderByAsc"));*/

        //final Projection projection = Projection.ProjectionBuilder.from(EntityMetaData.of(Pessoa.class), URLParaTest.getQueryParams("www.asdf.com?propriedade.id.$is=" + new ObjectId(new Date()) + "&propriedade.descricao.$is=asdf&propriedade.cor.nome.$is=558&propriedade.cor.teste.nome.$is=558&.$or=[afd:5;;df:78]"));
        final Projection projection = Projection.ProjectionBuilder.from(EntityMetaData.of(Pessoa.class), URLParaTest.getQueryParams("www.asdf.com?propriedade.id.$is=" + new ObjectId(new Date()) + "&propriedade.descricao.$is=asdf&propriedade.cor.nome.$is=558&propriedade.cor.teste.nome.$is=558"));
        //projection.getPipeline()
        final List<AggregationOperation> operations = projection.getPipeline();
        operations.forEach(it -> {
            it.toPipelineStages(DEFAULT_CONTEXT).forEach(iit -> {
                System.out.println(iit.toJson());
            });
        });
        System.out.println(operations);
        //operations.forEach(it -> BasicDBObject);

        //System.out.println(new Query(Criteria.where("sdf").is("tt")).toString());
    }

    @Test
    public void test2() throws MalformedURLException {
     /*   final URL url = new URL("http://www.test.com?nome.$contains=5&nome.$contains=asdf");
        //System.out.println(splitQuery(url));
        System.out.println(getQueryParams(url.toString()));
        getQueryParams(url.toString()).entrySet()
                .stream()
                .forEach(entr -> {
                    System.out.println("key " + entr.getKey());
                    entr.getValue().stream().forEach(it -> System.out.println("value = " + it));
                });*/
        //Stream.of(Operator.values()).map(Operator::getRegularExpression).forEach(System.out::println);
        /*System.out.println(Stream.of(Operator.values()).map(Operator::getRegularExpression).collect(Collectors.joining("|")));
        System.out.println(QueryBuilder.replaceAllOperators("tetes.$orderByAsc"));*/

        //final Projection projection = Projection.ProjectionBuilder.from(EntityMetaData.of(Pessoa.class), URLParaTest.getQueryParams("www.asdf.com?propriedade.id.$is=" + new ObjectId(new Date()) + "&propriedade.descricao.$is=asdf&propriedade.cor.nome.$is=558&propriedade.cor.teste.nome.$is=558&.$or=[afd:5;;df:78]"));
        final Projection2 criteria = Projection2.ProjectionBuilder.from(EntityMetaData.of(Pessoa.class), URLParaTest.getQueryParams("www.asdf.com?propriedade.id.$is=" + new ObjectId(new Date()) + "&propriedade.descricao.$is=asdf&propriedade.cor.nome.$is=558&propriedade.cor.teste.nome.$is=558&.$or=[afd:5;;df:78]"));
        final Projection2 aggregation = Projection2.ProjectionBuilder.from(EntityMetaData.of(Pessoa.class), URLParaTest.getQueryParams("www.asdf.com?propriedade.id.$is=" + new ObjectId(new Date()) + "&propriedade.descricao.$is=asdf&propriedade.cor.nome.$is=558&propriedade.cor.teste.nome.$is=558&.$or=[afd:5;;df:78]"));
        final Projection2 query = Projection2.ProjectionBuilder.from(EntityMetaData.of(Pessoa.class), URLParaTest.getQueryParams("www.asdf.com?propriedade.id.$is=" + new ObjectId(new Date()) + "&propriedade.descricao.$is=asdf&propriedade.cor.nome.$is=558&propriedade.cor.teste.nome.$is=558&.$or=[afd:5;;df:78]"));
        final Projection3 query3 = Projection3.ProjectionBuilder.from(EntityMetaData.of(Pessoa.class), URLParaTest.getQueryParams("www.asdf.com?propriedade.descricao.$is=asd&$or=[afd:'5';$or:[at:'25';$or:[hh:'855']];df:'78']"));

        /*query3.getCriteria().forEach(it -> {
            match(it).toPipelineStages(DEFAULT_CONTEXT).forEach(iit -> {
                System.out.println(iit.toJson());
            });
        });*/

        System.out.println("####################################");
        query3.getAggregations().forEach(it -> {
            it.toPipelineStages(DEFAULT_CONTEXT).forEach(iit -> {
                System.out.println(iit.toJson());
            });
        });
        System.out.println("####################################");
        query3.getQuery().forEach(it -> {
            it.toPipelineStages(DEFAULT_CONTEXT).forEach(iit -> {
                System.out.println(iit.toJson());
            });
        });
        //query3.getCriteria();
        //System.out.println(operations);
        //operations.forEach(it -> BasicDBObject);

        //System.out.println(new Query(Criteria.where("sdf").is("tt")).toString());
    }

    @Test
    public void testIndex() {
        final String t = "tews.tee.t.asdf";

        System.out.println(t);
        this.testIndex1(t);
    }

    void testIndex1(final String s) {
        if (s.contains(".")) {
            this.testIndex1(s.substring(s.indexOf(".") + 1));
            System.out.println(s.substring(0, s.indexOf(".")));
        } else {
            System.out.println(s);
        }
    }
}
