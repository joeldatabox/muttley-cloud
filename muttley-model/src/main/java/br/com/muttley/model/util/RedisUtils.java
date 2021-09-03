package br.com.muttley.model.util;

import br.com.muttley.model.security.User;

/**
 * @author Joel Rodrigues Moreira on 02/09/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class RedisUtils {
    public static String createKeyByOwner(final User user, final Class clazz, final String key) {
        return clazz.getName().toUpperCase() + ":" + user.getCurrentOwner().getId() + ":" + key;
    }
}
