package br.com.muttley.mongo.infra.metadata;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.beans.Transient;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String DATE_REGEX = "(\\d{4}|\\d{5}|\\d{6}|\\d{7})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|[3][01])";
    private static final String DATE_TIME_REGEX = "(\\d{4}|\\d{5}|\\d{6}|\\d{7})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|[3][01])T(00|01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23):(00|01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59):(00|01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59)([.])\\d{3}([+-])\\d{4}";
    private static final Map<String, EntityMetaData> cache = new HashMap<>();
    private String nameField;
    private Class classType;
    private boolean id;
    private EntityMetaDataType type;
    private Set<EntityMetaData> fields;
    private String collection;

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

    public boolean isDBRef() {
        return EntityMetaDataType.DBREF.equals(this.type);
    }

    public static EntityMetaData of(final Class type) {
        return of(type.getName(), type);
    }

    public static EntityMetaData of(final String name, final Class type) {
        if (isBasicObject(type)) {
            return new EntityMetaData().setNameField(name);
        } else {
            if (cache.containsKey(type.getName())) {
                return cache.get(type.getName());
            }
        }
        final EntityMetaData metaData = new EntityMetaData().setNameField(name);
        cache.put(type.getName(), metaData);

        final Document document = (Document) type.getAnnotation(Document.class);
        if (document != null) {
            metaData.setCollection(document.collection());
        }
        Stream.of(getAllFields(type))
                .filter(field -> field.getDeclaredAnnotation(Transient.class) == null || field.getDeclaredAnnotation(org.springframework.data.annotation.Transient.class) == null)
                .map(field -> {
                    final EntityMetaData ent = EntityMetaData.of(field.getName(), field.getType()).setType(EntityMetaDataType.of(field)).setClassType(field.getType());
                    if (field.getAnnotation(Id.class) != null) {
                        ent.setId(true);
                    }
                    return ent;
                })
                .forEach(entityMetaData -> {
                    metaData.addField(entityMetaData);
                });


        return metaData;


        /*final Field[] fields = FieldUtils.getAllFields(type);
        this.fields = Stream.of(fields)
                .filter(field -> field.getDeclaredAnnotation(Transient.class) == null)
                .map(field -> {
                    if (cache.containsKey(field.getType().getName())) {
                        return cache.get(field.getType().getName());
                    }
                    final EntityMetaData entityMetaData = new EntityMetaData();
                    entityMetaData.setNameField(field.getName());
                    if (!isBasicObject(field.getType())) {
                        final EntityMetaData other = new EntityMetaData(field.getType());
                        entityMetaData.setFields(other.getFields());
                        cache.put(field.getType().getName(), other);
                    }
                    //fieldMetaData.setName(field.getName());
                    return entityMetaData;
                }).collect(Collectors.toSet());*/
    }

    private EntityMetaData getFieldByName(final String nameField, final EntityMetaData entityMetaData) {
        //se o nome tiver . é necessário fazezr recursividade1
        if (nameField.contains(".")) {
            //pegando o primeiro nome
            final String currentPath = nameField.substring(0, nameField.indexOf("."));

            final EntityMetaData currentEntityMetaData = entityMetaData.getFields().stream().filter(it -> it.nameField.equals(currentPath))
                    .findFirst()
                    .orElse(null);
            if (currentEntityMetaData != null) {
                return getFieldByName(nameField.replace(currentPath + ".", ""), currentEntityMetaData);
            }
            return null;
        } else {
            if (entityMetaData.getFields() == null) {
                return null;
            }
            return entityMetaData
                    .getFields()
                    .stream()
                    .filter(it -> it.nameField.equals(nameField))
                    .findFirst()
                    .orElse(null);
        }
    }

    public EntityMetaData getFieldByName(final String nameField) {
        return getFieldByName(nameField, this);
    }

    public Object converteValue(Object object) {
        if (object == null || this.classType == object.getClass()) {
            return object;
        }
        switch (this.type) {
            case STRING:
            case ARRAY:
            case DBREF:
            case OTHER:
                return object;
            case BOOLEAN:
                final String objectSTR = object.toString();
                if ((objectSTR.equalsIgnoreCase("true") || objectSTR.equalsIgnoreCase("false"))) {
                    return Boolean.valueOf(objectSTR);
                }
                return object;
            case DATE: {
                try {
                    final String objectStr = object.toString();
                    if (objectStr.matches(DATE_REGEX)) {
                        return new SimpleDateFormat(DATE_PATTERN).parse(objectStr);
                    } else if (objectStr.matches(DATE_TIME_REGEX)) {
                        return new SimpleDateFormat(DATE_TIME_PATTERN).parse(objectStr);
                    }
                } catch (ParseException e) {
                }
                return object;
            }
            case NUMBER: {
                if ((this.classType == int.class) || (this.classType == Integer.class)) {
                    return Integer.valueOf(object.toString());
                }
                if ((this.classType == long.class) || (this.classType == Long.class)) {
                    return Long.valueOf(object.toString());
                }
                if ((this.classType == float.class) || (this.classType == Float.class)) {
                    return Float.valueOf(object.toString());
                }
                if ((this.classType == double.class) || (this.classType == Double.class)) {
                    return Double.valueOf(object.toString());
                }
                if (this.classType == BigDecimal.class) {
                    return new BigDecimal(object.toString());
                }
                if ((this.classType == short.class) || (this.classType == Short.class)) {
                    return Short.valueOf(object.toString());
                }
                if ((this.classType == BigInteger.class)) {
                    return BigInteger.valueOf(Long.valueOf(object.toString()));
                }
                return object;
            }
            default:
                return object;
        }
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
            System.out.println(clazz.getName());
            return false;
        }
    }

    public static void toPipelineOperation(final String key, final String value, final EntityMetaData entityMetaData) {

    }
}
