package br.com.muttley.mongo.query;

import br.com.muttley.mongo.query.url.paramvalue.NewQueryParam;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 24/10/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class URLParaTest {

    @Test
    public void testParams() {
        final String urlParams = "www.teste.com?nome=kasdfasd&idade=50&$or=|val=asdfa;;b=70|&$or=|val=asdfa;;b=70|";

        final List<NewQueryParam> params = getQueryParams(urlParams);

        for (final NewQueryParam queryParam : params) {
            System.out.println(queryParam.getKey() + " => " + queryParam.getValue());
            if(queryParam.isArrayValue()){
                queryParam.valueToArray().forEach(it -> {
                    System.out.println(it.getKey() + " => " + it.getValue());
                });
            }
        }
    }


    public static List<NewQueryParam> getQueryParams(String url) {
        //try {
        List<NewQueryParam> params = new LinkedList<>();
        String[] urlParts = url.split("\\?");
        if (urlParts.length > 1) {
            String query = urlParts[1];
            for (String param : query.split("&")) {
                params.add(new NewQueryParam(param));
            }
        }

        return params;
        /*} catch (UnsupportedEncodingException ex) {
            throw new AssertionError(ex);
        }*/
    }

}
