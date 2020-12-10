package br.com.muttley.mongo.service.infra.metadata;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.model.security.Owner;
import com.mongodb.BasicDBObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.mapping.Document;

import java.beans.Transient;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.apache.commons.lang3.reflect.FieldUtils.getAllFields;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;

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
public class EntityMetaData implements Cloneable {
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    private static final String DATE_REGEX = "(\\d{4}|\\d{5}|\\d{6}|\\d{7})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|[3][01])";
    private static final String DATE_TIME_REGEX = "(\\d{4}|\\d{5}|\\d{6}|\\d{7})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|[3][01])T(00|01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23):(00|01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59):(00|01|02|03|04|05|06|07|08|09|10|11|12|13|14|15|16|17|18|19|20|21|22|23|24|25|26|27|28|29|30|31|32|33|34|35|36|37|38|39|40|41|42|43|44|45|46|47|48|49|50|51|52|53|54|55|56|57|58|59)([.])\\d{3}([+-])\\d{4}";
    private static final Map<String, EntityMetaData> cache = new LinkedHashMap<>();
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
            if (EntityMetaData.cache.containsKey(type.getName())) {
                return EntityMetaData.cache.get(type.getName()).clone().setNameField(name);
            }
        }
        final EntityMetaData metaData = new EntityMetaData().setNameField(type.getName());
        EntityMetaData.cache.put(type.getName(), metaData);

        final Document document = (Document) type.getAnnotation(Document.class);
        if (document != null) {
            metaData.setCollection(document.collection());
        }
        Stream.of(getAllFields(type))
                .filter(field ->
                        !Modifier.isStatic(field.getModifiers()) && (field.getDeclaredAnnotation(Transient.class) == null && field.getDeclaredAnnotation(org.springframework.data.annotation.Transient.class) == null)
                )
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


        return metaData.clone().setNameField(name);
    }

    private static EntityMetaData getFieldByName(final String nameField, final EntityMetaData entityMetaData) {
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
                    .filter(it -> {
                        final String referencedName = "$" + it.nameField;
                        return it.nameField.equals(nameField) || referencedName.equals(nameField);
                    })
                    .findFirst()
                    .orElse(null);
        }
    }

    public EntityMetaData getFieldByName(final String nameField) {
        return getFieldByName(nameField, this);
    }

    private Set<EntityMetaData> getAllFieldFromLevelOf(final String nameField, final EntityMetaData entityMetaData) {
        //se o nome tiver . é necessário fazer recursividade
        if (nameField.contains(".")) {
            //pegando o primeiro nome
            final String currentPath = nameField.substring(0, nameField.indexOf("."));

            final EntityMetaData currentEntityMetaData = entityMetaData.getFields().stream().filter(it -> it.nameField.equals(currentPath))
                    .findFirst()
                    .orElse(null);
            if (currentEntityMetaData != null) {
                return getAllFieldFromLevelOf(nameField.replace(currentPath + ".", ""), currentEntityMetaData);
            }
            return null;
        } else {
            return Collections.unmodifiableSet(entityMetaData.getFields());
        }
    }

    public Set<EntityMetaData> getAllFieldFromLevelOf(final String nameField) {
        return getAllFieldFromLevelOf(nameField, this);
    }

    public Object converteValue(Object object) {
        if (object == null || this.classType == object.getClass()) {
            if (isId()) {
                return new ObjectId(object.toString());
            }
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
            return false;
        }
    }

    public List<AggregationOperation> createProjectFor(final String key) {
        //verificando se tem niveis de navegação no objeto
        if (!key.contains(".")) {
            //recuperando o campo espécifico
            EntityMetaData field = getFieldByName(key, this);
            //se não for DBRef podemos retornar null
            if (!field.isDBRef()) {
                return null;
            }

            //criando as operações necessárias
            return asList(
                    project(key).and(context -> new BasicDBObject("$objectToArray", "$" + field.getNameField())).as(field.getNameField()),
                    project(key).and(context -> new BasicDBObject("$arrayElemAt", asList("$" + field.getNameField() + ".v", 1))).as(field.getNameField()),
                    lookup(field.getCollection(), field.getNameField(), "_id", field.getNameField()),
                    unwind("$" + field.getNameField())
            );
        } else {
            /*//gerando um array de keys
            final String[] basicKeys = key.split("\\.");
            //gerando as keys necessárias para se buscar cada campo
            //cada key deve ser precedida ple key anterior
            final String[] keys = new String[basicKeys.length];
            //inserindo a primeira key
            keys[0] = basicKeys[0];
            //gerando as demais keys
            //como o primeiro item já foi inserido, podemos pular o mesmo
            for (int i = 1; i < basicKeys.length; i++) {
                //pegando o indice anterior e concatenando com o atual
                keys[i] = keys[i - 1] + "." + basicKeys[i];
            }*/

            return createOperation(generateCascadKeys(key), this);
        }
    }

    public static String[] generateCascadKeys(final String key) {
        //gerando um array de keys
        final String[] basicKeys = key.split("\\.");
        //gerando as keys necessárias para se buscar cada campo
        //cada key deve ser precedida ple key anterior
        final String[] keys = new String[basicKeys.length];
        //inserindo a primeira key
        keys[0] = basicKeys[0];
        //gerando as demais keys
        //como o primeiro item já foi inserido, podemos pular o mesmo
        for (int i = 1; i < basicKeys.length; i++) {
            //pegando o indice anterior e concatenando com o atual
            keys[i] = keys[i - 1] + "." + basicKeys[i];
        }
        return keys;
    }

    private List<AggregationOperation> createOperation(final String[] keyEntityMetaData, final EntityMetaData entityMetaData) {
        if (keyEntityMetaData == null || keyEntityMetaData.length == 0 || (keyEntityMetaData.length == 2 && keyEntityMetaData[1].endsWith(".$id"))) {
            return asList();
        }
        final List<AggregationOperation> result = new ArrayList<>();
        for (int i = 0; i < keyEntityMetaData.length; i++) {
            final EntityMetaData currentField = entityMetaData.getFieldByName(keyEntityMetaData[i]);
            if (currentField != null && currentField.isDBRef()) {
                if (currentField.getClassType() == Owner.class) {
                    throw new MuttleyBadRequestException(currentField.getClassType(), currentField.getNameField(), "Acesso indevido a propriedade");
                }
                //auxilia na concatenação
                final int aux = i;
                if (i == 0) {
                    //pegando todos os campos da classe para adicionar no project
                    final String[] keysForProject = getKeyForProject(keyEntityMetaData[i], entityMetaData);

                    result.addAll(
                            asList(
                                    project(keysForProject).and(context -> new BasicDBObject("$objectToArray", "$" + currentField.getNameField())).as(currentField.getNameField()),
                                    project(keysForProject).and(context -> new BasicDBObject("$arrayElemAt", asList("$" + currentField.getNameField() + ".v", 1))).as(currentField.getNameField()),
                                    lookup(currentField.getCollection(), currentField.getNameField(), "_id", currentField.getNameField()),
                                    unwind("$" + currentField.getNameField())
                            )
                    );
                } else {
                    //pengando o nome de variavle de cada classe
                    final List<String[]> keysForProject = new ArrayList<>(i);
                    for (int b = 0; b <= i; b++) {
                        keysForProject.add(getKeyForProject(keyEntityMetaData[b], entityMetaData));
                    }
                    ProjectionOperation projectToArray = null;
                    ProjectionOperation projectElemAt = null;
                    //gerando os projects
                    for (int b = 0; b < keysForProject.size(); b++) {
                        if (projectToArray == null) {
                            projectToArray = project(keysForProject.get(b));
                            projectElemAt = project(keysForProject.get(b));
                        } else {
                            final int aux1 = b;
                            final String[] keysNested = Stream.of(keysForProject.get(b)).map(it -> "$" + keyEntityMetaData[aux1 - 1] + "." + it)
                                    .filter(it -> !it.contains("$" + keyEntityMetaData[aux1]))
                                    .toArray(String[]::new);

                            projectToArray = projectToArray.and(keyEntityMetaData[b - 1]).nested(Fields.fields(keysNested));
                            projectElemAt = projectElemAt.and(keyEntityMetaData[b - 1]).nested(Fields.fields(keysNested));
                        }
                    }
                    //adicionando as informações necessárias para lookup
                    result.addAll(
                            asList(
                                    projectToArray.and(context -> new BasicDBObject("$objectToArray", "$" + keyEntityMetaData[aux])).as(keyEntityMetaData[aux]),
                                    projectElemAt.and(context -> new BasicDBObject("$arrayElemAt", asList("$" + keyEntityMetaData[aux] + ".v", 1))).as(keyEntityMetaData[aux]),
                                    lookup(currentField.getCollection(), keyEntityMetaData[aux], "_id", keyEntityMetaData[aux]),
                                    unwind("$" + keyEntityMetaData[aux])
                            )
                    );
                }
            }
        }
        return result;
    }

    /**
     * retorn todos os campos da classe para adicionar no project
     */
    private String[] getKeyForProject(final String key, final EntityMetaData entityMetaData) {

        return entityMetaData.getAllFieldFromLevelOf(key).stream().map(it -> it.getNameField()).toArray(String[]::new);

    }

    @Getter
    @EqualsAndHashCode(of = "key")
    private class KeyEntityMetaData {
        private final String key;
        private final EntityMetaData entityMetaData;

        public KeyEntityMetaData(String key, EntityMetaData entityMetaData) {
            this.key = key;
            this.entityMetaData = entityMetaData;
        }

    }

    @Override
    protected EntityMetaData clone() {
        try {
            return (EntityMetaData) super.clone();
        } catch (final CloneNotSupportedException ex) {
            throw new MuttleyBadRequestException(ex);
        }
    }
}
