package br.com.muttley.mongo.query;

import br.com.muttley.mongo.infra.newagregation.paramvalue.QueryParam;
import br.com.muttley.mongo.infra.newagregation.paramvalue.QueryParamImpl;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Joel Rodrigues Moreira on 24/10/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class URLParaTest {

    @Test
    public void testParams() {
        final String urlParams = "www.teste.com?nome=kasdfasd&idade=50&$or=[val:asdfa;;b:70]&$or=[val:asdfa;;b:70]";


    }


    public static List<QueryParam> getQueryParams(String url) {
        //try {
        return QueryParam.BuilderFromURL.newInstance().fromURL(url).build();
        /*List<QueryParamImpl> params = new LinkedList<>();
        String[] urlParts = url.split("\\?");
        if (urlParts.length > 1) {
            String query = urlParts[1];
            for (String param : query.split("&")) {
                params.add(new QueryParamImpl(param));
            }
        }*/


        //return params;
        /*} catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }*/
    }

    @Test
    public void testSplit(){
        Stream.of("test".split("\\.")).forEach(System.out::println);
        Stream.of("test1.test2".split("\\.")).forEach(System.out::println);
        //System.out.println();
    }

}
