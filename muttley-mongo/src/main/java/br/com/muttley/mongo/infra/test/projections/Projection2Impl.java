package br.com.muttley.mongo.infra.test.projections;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 02/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class Projection2Impl implements Projection2 {
    private int level = 0;
    private int orderControl = 0;
    private int levelControl = 0;
    private EntityMetaData entityMetaData;
    private EntityMetaData parentEntityMetadata;
    private String property;
    private String compositePropertyWithFather = "";//nome da propriedade pai em cascata
    private List<Projection2Impl> subproperties;
    private List<Criterion2Impl> criterions;
    private boolean generatedLookup = false;//indica se a propriedade atual já fez lookup ou não

    protected Projection2Impl() {
    }

    private void setParent(final EntityMetaData entityMetaData) {
        if (!this.subpropertiesIsEmpty()) {
            this.subproperties
                    .parallelStream()
                    .forEach(it -> {
                        it.parentEntityMetadata = entityMetaData;
                        it.setParent(entityMetaData);
                    });
        }
    }

    @Override
    public Projection2 addProjection(Projection2 projection, EntityMetaData entityMetaData, String property, Criterion2 criterion) {
        final Projection2Impl result = this.addProjection((Projection2Impl) projection, entityMetaData, property, criterion);
        result.setParent(entityMetaData);
        return result;
    }

    private Projection2Impl addProjection(final Projection2Impl projection, final EntityMetaData entityMetaData, final String property, final Criterion2 criterion) {
        if (projection.containsProperty(property)) {
            //adiciona os demais criterios aqui
            projection.addCriterion((Criterion2Impl) criterion);
        } else if (property.contains(".")) {
            //pegando o nome atual da chave
            final String currentProperty = property.substring(0, property.indexOf("."));
            final Projection2Impl current;
            //já existe esse chave no nível atual?
            if (projection.containsProperty(currentProperty)) {
                //recuperando a chave no nível atual
                current = projection.getByProperty(currentProperty);
            } else {
                //se chegou aqui é sinal que não existe ainda essa propriedade no nível atual
                current = this.createNewSubProjection(currentProperty);
                current.property = currentProperty;
                current.entityMetaData = entityMetaData.getFieldByName(currentProperty);
                projection.addProjection(current);
            }
            current.addProjection(current, current.entityMetaData, property.substring(property.indexOf(".") + 1), criterion);
        } else {
            final Projection2Impl current = this.createNewSubProjection(property);
            current.property = property;
            current.entityMetaData = entityMetaData.getFieldByName(property);
            projection.addProjection(current);
            current.addCriterion((Criterion2Impl) criterion);
        }
        return projection;
    }

    @Override
    public List<AggregationOperation> getAggregations() {
        //se o campo atual não tem subcampos, logo devemos apenas aplicar o critério
        if (this.subpropertiesIsEmpty()) {
            return this.criterions.stream()
                    .map(it -> this.extractAggregations(it))
                    .reduce((acc, others) -> {
                        acc.addAll(others);
                        return acc;
                    }).orElse(new LinkedList<>());


            //talvez, tenhamos uma subpropriedade e ela seja um id apenas
            //tendo isso como base basta apenas aplicar um critério simples sem
            //processo de lookup
        }
        //verificando se a propriedade atual precisa de um lookup
        else if (this.isDBRef() && !this.generatedLookup) {
            final List<AggregationOperation> list = new LinkedList<>();
            //indica se o primeiro campo no where era o id. Se sim pularemos ele durante a interação
            //isso evitará que se repita condições já realizadas
            boolean skipFirstId = false;

            //contains subproperties
            if (!this.subpropertiesIsEmpty()) {
                //a primeira subproperty é um id?
                if (this.subproperties.get(0).isId()) {
                    //vamos adicionar isso no where sem fazer lookup
                    final Projection2Impl subProperty = this.subproperties.get(0);
                    list.addAll(
                            subProperty.criterions
                                    .stream()
                                    .map(it -> this.extractAggregations(subProperty.entityMetaData, subProperty.compositePropertyWithFather, subProperty.property, it))
                                    .reduce((acc, others) -> {
                                        acc.addAll(others);
                                        return acc;
                                    }).orElse(new LinkedList<>())
                    );
                    //sinalizando para pularmos o primeiro campo
                    skipFirstId = true;
                }
                //verificando se precisa fazer project
                if (this.subproperties.size() > 1 || !this.subproperties.get(0).isId()) {
                    list.addAll(this.parentEntityMetadata.createProjectFor(this.compositePropertyWithFather));
                    this.generatedLookup = true;//marcando que essa propriedade já foi fieta lookup
                }

                if (skipFirstId) {
                    list.addAll(this.extractPipelineForAggregations(this.subproperties.subList(1, this.subproperties.size()).stream()));
                } else {
                    list.addAll(this.extractPipelineForAggregations(this.subproperties.stream()));
                }
            }
            return list;
        } else {
            return this.subproperties
                    .stream()
                    .map(it -> it.getAggregations())
                    .reduce((acc, others) -> {
                        acc.addAll(others);
                        return acc;
                    }).orElse(new LinkedList<>());
        }
    }

    private List<AggregationOperation> extractPipelineForAggregations(final Stream<Projection2Impl> stream) {
        return stream
                .map(it -> it.getAggregations())
                .reduce((acc, others) -> {
                    acc.addAll(others);
                    return acc;
                }).orElse(null);
    }

    @Override
    public List<Criteria> getCriteria() {

        //se o campo atual não tem subcampos, logo devemos apenas aplicar o critério
        if (this.subpropertiesIsEmpty()) {
            return this.criterions.stream()
                    .map(it -> this.extractOperation(it))
                    .collect(toList());


            //talvez, tenhamos uma subpropriedade e ela seja um id apenas
            //tendo isso como base basta apenas aplicar um critério simples sem
            //processo de lookup
        }/* else if (!this.subpropertiesIsEmpty() && this.isDBRef() && this.subproperties.get(0).isId()) {
            final List<AggregationOperation> list = new LinkedList<>(this.subproperties.get(0).criterions.stream().map(it -> this.extractOperation(it)).collect(toList()));
            list.addAll(this.subproperties.subList(0, this.subproperties.size() - 1).stream().map(it -> it.getPipeline()).reduce((acc, others) -> {
                acc.addAll(others);
                return acc;
            }).orElse(null));
            return list;
            //verificando se a propriedade atual precisa de um lookup
        }*/ else if (this.isDBRef() && !this.generatedLookup) {
            final List<AggregationOperation> list = new LinkedList<>();
            //indica se o primeiro campo no where era o id. Se sim pularemos ele durante a interação
            //isso evitará que se repita condições já realizadas
            boolean skipFirstId = false;

            //contains subproperties
            if (!this.subpropertiesIsEmpty()) {
                //a primeira subproperty é um id?
                if (this.subproperties.get(0).isId()) {
                    //vamos adicionar isso no where sem fazer lookup
                    final ProjectionImpl subProperty = this.subproperties.get(0);
                    list.addAll(subProperty.criterions.stream().map(it -> this.extractOperation(subProperty.entityMetaData, subProperty.compositePropertyWithFather, subProperty.property, it)).collect(toList()));
                    //sinalizando para pularmos o primeiro campo
                    skipFirstId = true;
                }
                //verificando se precisa fazer project
                if (this.subproperties.size() > 1 || !this.subproperties.get(0).isId()) {
                    list.addAll(this.parentEntityMetadata.createProjectFor(this.compositePropertyWithFather));
                    this.generatedLookup = true;//marcando que essa propriedade já foi fieta lookup
                }

                if (skipFirstId) {
                    list.addAll(this.extractPipeline(this.subproperties.subList(1, this.subproperties.size()).stream()));
                } else {
                    list.addAll(this.extractPipeline(this.subproperties.stream()));
                }
            }
            return list;
        } else {
            return this.subproperties.stream().map(it -> it.getPipeline()).reduce((acc, others) -> {
                acc.addAll(others);
                return acc;
            }).orElse(null);
        }
        //return null;
    }

    private Projection2Impl addCriterion(final Criterion2Impl criterion) {
        if (criterion != null) {
            if (this.criterions == null) {
                this.criterions = new LinkedList<>();
            }
            this.criterions.add(criterion.setOrder(this.generateOrder()).setLevel(this.generateLevel()));
        }
        return this;
    }

    private Projection2Impl addProjection(final Projection2Impl projection) {
        if (projection != null) {
            if (this.subproperties == null) {
                this.subproperties = new LinkedList<>();
            }
            projection.parentEntityMetadata = this.entityMetaData;
            this.subproperties.add(projection);
        }
        return this;
    }

    private List<AggregationOperation> extractAggregations(final Criterion2 criterion) {
        return this.extractAggregations(this.entityMetaData, this.compositePropertyWithFather, this.property, criterion);
    }

    private List<AggregationOperation> extractAggregations(final EntityMetaData entityMetaData, final String compositePropertyWithFather, final String property, final Criterion2 criterion) {
        final String customNameProperty;
        final String customNameCompositePropertyWithFather;
        if (entityMetaData.isId() && property.equals("id")) {
            customNameProperty = "$" + property;
            if (compositePropertyWithFather.endsWith(".id")) {
                customNameCompositePropertyWithFather = compositePropertyWithFather.replace(".id", ".$id");
            } else {
                customNameCompositePropertyWithFather = compositePropertyWithFather;
            }
        } else {
            customNameProperty = property;
            customNameCompositePropertyWithFather = compositePropertyWithFather;
        }

        return criterion
                .getOperator()
                .extractAggregations(entityMetaData, customNameCompositePropertyWithFather, customNameProperty, criterion.getValue());
    }

    /**
     * Retorna um campo com base na chave passada como parametro
     */
    protected Projection2Impl getByProperty(final String key) {
        return this.getByProperty(key, this);
    }

    private Projection2Impl getByProperty(final String property, final Projection2Impl projection) {
        //se o nome tiver . é necessário fazezr recursividade1
        if (property.contains(".")) {
            //pegando o primeiro nome
            final String currentProperty = property.substring(0, property.indexOf("."));

            final Projection2Impl currentProjection;
            if (projection.subpropertiesIsEmpty()) {
                currentProjection = null;
            } else {
                currentProjection = projection.subproperties.parallelStream().filter(it -> it.property.equals(currentProperty))
                        .findFirst()
                        .orElse(null);
            }

            if (currentProjection != null) {
                return getByProperty(property.replace(currentProperty + ".", ""), currentProjection);
            }
            return null;
        } else {
            if (projection.subpropertiesIsEmpty()) {
                return null;
            }
            return projection
                    .subproperties
                    .parallelStream()
                    .filter(it -> {
                        final String referencedName = "$" + it.property;
                        return it.property.equals(property) || referencedName.equals(property);
                    }).findFirst()
                    .orElse(null);
        }
    }

    /**
     * Verifica se uma determinada propriedade já foi inserida como campos internos
     */
    protected boolean containsProperty(final String key) {
        return this.containsProperty(key, this);
    }

    private boolean containsProperty(final String property, final Projection2Impl projection) {
        //se o nome tiver . é necessário fazezr recursividade
        if (property.contains(".")) {
            //pegando o primeiro nome
            final String currentProperty = property.substring(0, property.indexOf("."));
            if (projection.subpropertiesIsEmpty()) {
                return false;
            }
            final Projection2Impl currentProjection =
                    projection.subproperties
                            .parallelStream()
                            .filter(it -> it.property.equals(currentProperty))
                            .findFirst()
                            .orElse(null);
            if (currentProjection != null) {
                return containsProperty(property.replace(currentProperty + ".", ""), currentProjection);
            }
            return false;
        } else {
            if (projection.subpropertiesIsEmpty()) {
                return false;
            }
            return projection
                    .subproperties
                    .parallelStream()
                    .filter(it -> {
                        final String referencedProperty = "$" + it.property;
                        return it.property.equals(property) || referencedProperty.equals(property);
                    }).count() > 0;
        }
    }

    protected boolean subpropertiesIsEmpty() {
        return isEmpty(this.subproperties);
    }


    protected boolean isDBRef() {
        return this.entityMetaData != null && this.entityMetaData.isDBRef();
    }

    protected boolean isId() {
        return this.entityMetaData != null && this.entityMetaData.isId();
    }

    /**
     * Cria uma nova incia de {@link Projection2Impl} mantendo o controle de level
     */
    private Projection2Impl createNewSubProjection(final String property) {
        final Projection2Impl current = new Projection2Impl();
        current.levelControl = this.generateLevel();
        current.level = this.generateLevel();
        if (!this.compositePropertyWithFather.equals("")) {
            current.compositePropertyWithFather = this.compositePropertyWithFather + "." + property;
        } else {
            current.compositePropertyWithFather = property;
        }
        return current;
    }

    private int generateOrder() {
        this.orderControl++;
        return this.orderControl;
    }

    private int generateLevel() {
        return this.levelControl + 1;
    }
}
