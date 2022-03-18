package br.com.muttley.annotations.valitators;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Joel Rodrigues Moreira on 17/03/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyFileUtils {

    /**
     * Retorna um InputStream apartir de uma URL
     * Caso tenha algum problema coma URL informada será retornado null automaticamente
     */
    public static InputStream fromURL(final String url) {
        try {
            return fromURL(new URL(url));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retorna um InputStream apartir de uma URL
     * Caso tenha algum problema coma URL informada será retornado null automaticamente
     */
    public static InputStream fromURL(final URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retorna um InputStream apartir de uma URL
     * Caso tenha algum problema coma URL informada será retornado um InputStream da URL padrão informada
     *
     * @param url        -> url que tentaremos resolver
     * @param urlDefault -> url que será usada caso de algum problema com a outra informada
     */
    public static InputStream fromURL(final String url, final String urlDefault) {
        final InputStream inputStream = fromURL(url);
        if (inputStream != null) {
            return inputStream;
        }
        return fromURL(urlDefault);
    }
}
