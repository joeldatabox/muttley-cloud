package br.com.muttley.model.util;

import java.util.Collection;
import java.util.Objects;

/**
 * @author Joel Rodrigues Moreira 17/11/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class CollectionUtils {
    public static boolean isEquals(final Collection first, final Collection second) {
        if ((first == null || first.isEmpty()) && (second == null || second.isEmpty())) {
            return true;
        }
        if ((first == null && second != null) || (first != null && second == null)) {
            return false;
        }
        if (first.size() != second.size()) {
            return false;
        }

        return first
                .parallelStream()
                .filter(f ->
                        second.parallelStream()
                                .filter(s -> Objects.equals(s, f))
                                .count() < 1
                ).count() == 0;
    }
}
