package br.com.muttley.mongo.service.infra.metadata;

import org.springframework.data.mongodb.core.mapping.DBRef;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 25/05/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public enum EntityMetaDataType {
    STRING,
    NUMBER,
    DBREF,
    BOOLEAN,
    DATE,
    ARRAY,
    OTHER;

    public static EntityMetaDataType of(Field field) {
        if (field.getType() == String.class) {
            return STRING;
        }
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            return BOOLEAN;
        }
        if (field.getType() == Date.class) {
            return DATE;
        }
        if (field.getAnnotation(DBRef.class) != null) {
            return DBREF;
        }
        if (field.getType() == List.class || field.getType() == Set.class || field.getType() == Collection.class) {
            return ARRAY;
        }
        if ((field.getType() == short.class) ||
                (field.getType() == int.class) ||
                (field.getType() == long.class) ||
                (field.getType() == float.class) ||
                (field.getType() == double.class) ||
                (field.getType() == Short.class) ||
                (field.getType() == Integer.class) ||
                (field.getType() == Long.class) ||
                (field.getType() == Float.class) ||
                (field.getType() == Double.class) ||
                (field.getType() == BigInteger.class) ||
                (field.getType() == BigDecimal.class)) {
            return NUMBER;
        }
        return OTHER;
    }
}
