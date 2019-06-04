package br.com.muttley.mongo.infra.metadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;

import java.beans.Transient;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.apache.commons.lang3.reflect.FieldUtils.getAllFields;

/**
 * @author Joel Rodrigues Moreira on 25/05/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "nameField")
@ToString
public class EntityMetaData {
    private static final Map<String, EntityMetaData> cache = new HashMap<>();
    private String nameField;
    private boolean id;
    private EntityMetaDataType type;
    private Set<EntityMetaData> fields;
    private String collection;
    private int level = 0;

    public EntityMetaData() {
    }

    public EntityMetaData addFields(Collection<EntityMetaData> values) {
        if (this.fields == null) {
            this.fields = new HashSet<>();
        }
        this.fields.addAll(values);
        return this;
    }

    public EntityMetaData addField(final EntityMetaData entityMetaData) {
        if (this.fields == null) {
            this.fields = new HashSet();
        }
        this.fields.add(entityMetaData);
        return this;
    }

    public static EntityMetaData of(final String name, final Class type) {
        return of(name, type, 0);
    }

    private static EntityMetaData of(final String name, final Class type, int level) {
        if (isBasicObject(type)) {
            return new EntityMetaData().setNameField(name).setLevel(level);
        } else {
            if (cache.containsKey(type.getName())) {
                return cache.get(type.getName()).setLevel(level +1);
            }
        }
        final EntityMetaData metaData = new EntityMetaData().setNameField(name).setLevel(level);
        cache.put(type.getName(), metaData);

        final Document document = (Document) type.getAnnotation(Document.class);
        if (document != null) {
            metaData.setCollection(document.collection());
        }
        Stream.of(getAllFields(type))
                .filter(field -> field.getDeclaredAnnotation(Transient.class) == null || field.getDeclaredAnnotation(org.springframework.data.annotation.Transient.class) == null)
                .map(field -> {
                    final EntityMetaData ent = EntityMetaData.of(field.getName(), field.getType(), level + 1).setType(EntityMetaDataType.of(field));
                    if (field.getAnnotation(Id.class) != null) {
                        ent.setId(true);
                    }
                    return ent;
                })
                .forEach(entityMetaData -> {
                    metaData.addField(entityMetaData);
                });
        return metaData;
    }

    public EntityMetaData(final MongoEntityInformation<?, String> mongoEntityInformation) {
        //this(mongoEntityInformation.getJavaType());
    }

    private static boolean isBasicObject(final Class clazz) {

        if ((clazz == byte.class) ||
                (clazz.isPrimitive()) ||
                (clazz == String.class) ||
                (clazz == Byte.class) ||
                (clazz == Short.class) ||
                (clazz == Integer.class) ||
                (clazz == Long.class) ||
                (clazz == Float.class) ||
                (clazz == Double.class) ||
                (clazz == Boolean.class) ||
                (clazz == Date.class) ||
                (clazz == BigDecimal.class) ||
                (clazz == BigInteger.class) ||
                (clazz.isEnum()) ||
                (clazz.isArray()) ||
                (clazz == Set.class) ||
                (clazz == Collection.class) ||
                (clazz == Object.class)) {
            return true;
        } else {
            return false;
        }
    }
}
