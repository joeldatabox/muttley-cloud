package br.com.muttley.model.util;

import org.bson.types.ObjectId;

/**
 * @author Joel Rodrigues Moreira on 04/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ObjectIdUtils {
    public static ObjectId createOf(final String id) {
        return new ObjectId(id);
    }
}
