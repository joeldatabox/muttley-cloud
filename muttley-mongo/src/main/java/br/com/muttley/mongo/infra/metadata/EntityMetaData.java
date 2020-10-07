package br.com.muttley.mongo.infra.metadata;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.model.security.Owner;
import com.mongodb.BasicDBObject;
import lombok.AccessLevel;
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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static br.com.muttley.mongo.views.source.ViewSource._TRUE;
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
    private static final Map<String, EntityMetaData> cache = new HashMap<>();
    @Setter(AccessLevel.PRIVATE)
    private String nameField;
    @Setter(AccessLevel.PRIVATE)
    private Class classType;
    @Setter(AccessLevel.PRIVATE)
    private boolean id;
    @Setter(AccessLevel.PRIVATE)
    private EntityMetaDataType type;
    @Setter(AccessLevel.PRIVATE)
    private Set<EntityMetaData> fields;
    @Setter(AccessLevel.PRIVATE)
    private String collection;
    ;

    private EntityMetaData() {
    }

    private EntityMetaData addFields(Collection<EntityMetaData> values) {
        if (this.fields == null) {
            this.fields = new LinkedHashSet<>();
        }
        this.fields.addAll(values);
        return this;
    }

    public Set<EntityMetaData> getFields() {
        return Collections.unmodifiableSet(fields);
    }

    private EntityMetaData addField(final EntityMetaData entityMetaData) {
        if (this.fields == null) {
            this.fields = new LinkedHashSet<>();
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

    private static EntityMetaData of(final String name, final Class type) {
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
                    final EntityMetaData ent;
                    if (isBasicObject(field.getType())) {
                        ent = EntityMetaData.of(field.getName(), field.getType())
                                .setType(EntityMetaDataType.of(field))
                                .setClassType(field.getType());
                    } else {
                        ent = EntityMetaData.of(field.getName(), field.getType())
                                .clone()
                                .setNameField(field.getName())
                                .setType(EntityMetaDataType.of(field))
                                .setClassType(field.getType());
                    }
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

    private static EntityMetaData getFieldByName(final String nameField, final EntityMetaData entityMetaData) {
        //se o nome tiver . é necessário fazezr recursividade1
        if (nameField.contains(".")) {
            //pegando o primeiro nome
            final String currentPath = nameField.substring(0, nameField.indexOf("."));

            final EntityMetaData currentEntityMetaData = entityMetaData.fields.stream().filter(it -> it.nameField.equals(currentPath))
                    .findFirst()
                    .orElse(null);
            if (currentEntityMetaData != null) {
                return getFieldByName(nameField.replace(currentPath + ".", ""), currentEntityMetaData);
            }
            return null;
        } else {
            if (entityMetaData.fields == null) {
                return null;
            }
            return entityMetaData
                    .fields
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
        final EntityMetaData entityMetaData = getFieldByName(nameField, this);
        if (entityMetaData != null) {
            return entityMetaData;
        } else if (this.getNameField().equals(nameField)) {
            return this;
        }
        return null;
    }

    private Set<EntityMetaData> getAllFieldFromLevelOf(final String nameField, final EntityMetaData entityMetaData) {
        //se tiver null retornamos os campos do level atual
        if (nameField == null) {
            return Collections.unmodifiableSet(entityMetaData.fields);
        }
        //se o nome tiver . é necessário fazer recursividade
        if (nameField.contains(".")) {
            //pegando o primeiro nome
            final String currentPath = nameField.substring(0, nameField.indexOf("."));

            final EntityMetaData currentEntityMetaData = entityMetaData.fields.parallelStream().filter(it -> it.nameField.equals(currentPath))
                    .findFirst()
                    .orElse(null);
            if (currentEntityMetaData != null) {
                return getAllFieldFromLevelOf(nameField.replace(currentPath + ".", ""), currentEntityMetaData);
            }
            return null;
        } else {
            return Collections.unmodifiableSet(entityMetaData.fields);
        }
    }

    private Set<EntityMetaData> getAllSubFieldsFrom(final String nameField, final EntityMetaData entityMetaData) {
        //se tiver null retornamos os campos do level atual
        if (nameField == null) {
            return Collections.unmodifiableSet(entityMetaData.fields);
        }
        //se o nome tiver . é necessário fazer recursividade
        if (nameField.contains(".")) {
            //pegando o primeiro nome
            final String currentPath = nameField.substring(0, nameField.indexOf("."));

            final EntityMetaData currentEntityMetaData = entityMetaData.fields.parallelStream().filter(it -> it.nameField.equals(currentPath))
                    .findFirst()
                    .orElse(null);
            if (currentEntityMetaData != null) {
                return getAllFieldFromLevelOf(nameField.replace(currentPath + ".", ""), currentEntityMetaData);
            }
            return null;
        } else {
            final EntityMetaData currentEntityMetaData = entityMetaData.fields
                    .parallelStream()
                    .filter(it -> it.nameField.equals(nameField))
                    .findFirst()
                    .orElse(null);
            if (currentEntityMetaData != null) {
                return Collections.unmodifiableSet(currentEntityMetaData.fields);
            }
            return Collections.unmodifiableSet(entityMetaData.fields);
        }
    }

    public Set<EntityMetaData> getAllFieldFromLevelOf(final String nameField) {
        return getAllFieldFromLevelOf(nameField, this);
    }

    public Object converteValue(Object object) {
        if (object == null || this.classType == object.getClass()) {
            if (isId() && ObjectId.isValid(object.toString())) {
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

    public List<AggregationOperation> createProject() {
        return this.createProjectFor(null);
    }

    public List<AggregationOperation> createProjectFor(final String key) {
        //se a key for null logo devemos fazer lookup para o campo atual
        /*if (key == null) {
            return createOperation(new String[]{this.getNameField()}, this);*/
        //criando as operações necessárias
            /*return asList(
                    project(this.getNameField()).and(context -> new org.bson.Document("$objectToArray", "$" + this.getNameField())).as(this.getNameField()),
                    project(this.getNameField()).and(context -> new org.bson.Document("$arrayElemAt", asList("$" + this.getNameField() + ".v", 1))).as(this.getNameField()),
                    lookup(this.getCollection(), this.getNameField(), "_id", this.getNameField()),
                    unwind("$" + this.getNameField())
            );*/
        /*}*/
        //verificando se tem niveis de navegação no objeto
        /*else if (!key.contains(".")) {*/
        //recuperando o campo espécifico
        //EntityMetaData field = getFieldByName(key, this);
        //se não for DBRef podemos retornar null
            /*if (!field.isDBRef()) {
                return emptyList();
            }*/

            /*//criando as operações necessárias
            return asList(
                    project(key).and(context -> new org.bson.Document("$objectToArray", "$" + field.getNameField())).as(field.getNameField()),
                    project(key).and(context -> new org.bson.Document("$arrayElemAt", asList("$" + field.getNameField() + ".v", 1))).as(field.getNameField()),
                    lookup(field.getCollection(), field.getNameField(), "_id", field.getNameField()),
                    unwind("$" + field.getNameField())
            );*/
       /*     return createOperation(new String[]{key}, this);
        } else {*/
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

        return createOperationOther(keys, this);
        //}
    }

    private List<AggregationOperation> createOperationOther(final String[] keyEntityMetaData, final EntityMetaData entityMetaData) {
        if (keyEntityMetaData == null || keyEntityMetaData.length == 0 || (keyEntityMetaData.length == 2 && keyEntityMetaData[1].endsWith(".$id"))) {
            return asList();
        }
        final List<AggregationOperation> result = new LinkedList<>();
        for (int i = 0; i < keyEntityMetaData.length; i++) {
            final EntityMetaData currentField = entityMetaData.getFieldByName(keyEntityMetaData[i]);
            if (currentField != null && currentField.isDBRef()) {
                if (currentField.getClassType() == Owner.class) {
                    throw new MuttleyBadRequestException(currentField.getClassType(), currentField.getNameField(), "Acesso indevido a propriedade");
                }

                //se tem apenas um nível basta apenas gerarmos o lookup diretamente
                if (keyEntityMetaData.length == 1) {
                    //pegando todos os campos da classe para adicionar no project
                    final String[] keysForProject = getKeyForProject(keyEntityMetaData[i], entityMetaData);
                    result.addAll(
                            asList(
                                    project(keysForProject).and(context -> new org.bson.Document("$objectToArray", "$" + currentField.getNameField())).as(currentField.getNameField()),
                                    project(keysForProject).and(context -> new org.bson.Document("$arrayElemAt", asList("$" + currentField.getNameField() + ".v", 1))).as(currentField.getNameField()),
                                    lookup(currentField.getCollection(), currentField.getNameField(), "_id", currentField.getNameField()),
                                    unwind("$" + currentField.getNameField())
                            )
                    );
                } else {
                }

                System.out.println("campos iniciso [" + keyEntityMetaData[i] + "]" + currentField.getClassType());
                //Stream.of(keysForProject).forEach(System.out::println);
                System.out.println("campos fim [" + keyEntityMetaData[i] + "]" + currentField.getClassType());

                /*//auxilia na concatenação
                final int aux = i;
                if (i == 0) {
                    //pegando todos os campos da classe para adicionar no project
                    final String[] keysForProject = getKeyForProject(keyEntityMetaData[i], entityMetaData);

                    result.addAll(
                            asList(
                                    project(keysForProject).and(context -> new org.bson.Document("$objectToArray", "$" + currentField.getNameField())).as(currentField.getNameField()),
                                    project(keysForProject).and(context -> new org.bson.Document("$arrayElemAt", asList("$" + currentField.getNameField() + ".v", 1))).as(currentField.getNameField()),
                                    lookup(currentField.getCollection(), currentField.getNameField(), "_id", currentField.getNameField()),
                                    unwind("$" + currentField.getNameField())
                            )
                    );*/
            } else {
                   /* //pengando o nome de variavle de cada classe
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
                                    projectToArray.and(context -> new org.bson.Document("$objectToArray", "$" + keyEntityMetaData[aux])).as(keyEntityMetaData[aux]),
                                    projectElemAt.and(context -> new org.bson.Document("$arrayElemAt", asList("$" + keyEntityMetaData[aux] + ".v", 1))).as(keyEntityMetaData[aux]),
                                    lookup(currentField.getCollection(), keyEntityMetaData[aux], "_id", keyEntityMetaData[aux]),
                                    unwind("$" + keyEntityMetaData[aux])
                            )
                    );*/
            }

        }
        final List<BasicDBObject> object = new LinkedList();
        final List<BasicDBObject> object2 = new LinkedList();
        for (int a = 0; a < keyEntityMetaData.length; a++) {
            final String[] keysProject = getKeyForProject(keyEntityMetaData[a], entityMetaData);
            final BasicDBObject dbObject = new BasicDBObject();
            for (final String currentKey : keysProject) {
                dbObject.append(currentKey, _TRUE);
            }
            object.add(dbObject);
            object2.add((BasicDBObject) dbObject.clone());
        }
        if (object.size() > 1) {
            for (int i = 0; i < object.size(); i++) {
                if ((i + 1) < object.size()) {
                    final BasicDBObject dbObject = object.get(i);
                    final BasicDBObject dbObject2 = object2.get(i);
                    if (keyEntityMetaData[i].contains(".")) {
                        dbObject.append(keyEntityMetaData[i].substring(keyEntityMetaData[i].lastIndexOf(".") + 1), object.get(i + 1));
                        dbObject2.append(keyEntityMetaData[i].substring(keyEntityMetaData[i].lastIndexOf(".") + 1), object2.get(i + 1));
                    } else {
                        dbObject.append(keyEntityMetaData[i], object.get(i + 1));
                        dbObject2.append(keyEntityMetaData[i], object2.get(i + 1));
                    }
                }
            }
        }
        if (object.size() > 1) {
            final BasicDBObject dbObject = object.get(object.size() - 1);
            final BasicDBObject dbObject2 = object2.get(object2.size() - 1);
            object.removeIf(it -> !it.equals(object.get(0)));
            object2.removeIf(it -> !it.equals(object2.get(0)));
            //dbObject.keySet().parallelStream().forEach(it -> dbObject.removeField(it));
            //pegando o campo mais interno para lookup
            final String[] keys = keyEntityMetaData[keyEntityMetaData.length - 1].split("\\.");
            final String lastKey = keys[keys.length - 1];
            dbObject.append(lastKey, new BasicDBObject("$objectToArray", "$" + keyEntityMetaData[keyEntityMetaData.length - 1]));
            //{$arrayElemAt:["$user.v",1]}}},
            dbObject2.append(lastKey, new BasicDBObject("$arrayElemAt", asList("$" + keyEntityMetaData[keyEntityMetaData.length - 1] + ".v", 1)));

        } else {
        }

        System.out.println("itens list" + object);
        System.out.println("itens list" + object2);
        //}

        return result;
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
                                    project(keysForProject).and(context -> new org.bson.Document("$objectToArray", "$" + currentField.getNameField())).as(currentField.getNameField()),
                                    project(keysForProject).and(context -> new org.bson.Document("$arrayElemAt", asList("$" + currentField.getNameField() + ".v", 1))).as(currentField.getNameField()),
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
                                    projectToArray.and(context -> new org.bson.Document("$objectToArray", "$" + keyEntityMetaData[aux])).as(keyEntityMetaData[aux]),
                                    projectElemAt.and(context -> new org.bson.Document("$arrayElemAt", asList("$" + keyEntityMetaData[aux] + ".v", 1))).as(keyEntityMetaData[aux]),
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
            throw new MuttleyException(ex);
        }
    }
}
