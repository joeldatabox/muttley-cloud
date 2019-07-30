package br.com.muttley.model.util;

import java.util.Iterator;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

/**
 * @author Joel Rodrigues Moreira on 29/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class StreamUtil {
    public static <T> Stream<T> of(Iterator<T> sourceIterator) {
        return of(sourceIterator, false);
    }

    public static <T> Stream<T> of(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return stream(iterable.spliterator(), parallel);
    }
}
