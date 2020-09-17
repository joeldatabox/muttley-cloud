package br.com.muttley.mongo.query.projections;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.LinkedList;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 02/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ProjectionImpl implements Projection {
    private int level = 0;
    private int orderControl = 0;
    private int levelControl = 0;
    private EntityMetaData entityMetaData;
    private String property;
    private List<ProjectionImpl> subproperties;
    private List<CriterionImpl> criterions;
    private boolean generatedLookup = false;//indica se a propriedade atual já fez lookup ou não

    protected ProjectionImpl() {
    }

    @Override
    public Projection addProjection(final Projection projection, final EntityMetaData entityMetaData, final String property, final Criterion criterion) {
        return this.addProjection((ProjectionImpl) projection, entityMetaData, property, criterion);
    }

    private Projection addProjection(final ProjectionImpl projection, final EntityMetaData entityMetaData, final String property, final Criterion criterion) {
        if (projection.containsProperty(property)) {
            //adiciona os demais criterios aqui
            projection.addCriterion((CriterionImpl) criterion);
        } else if (property.contains(".")) {
            //pegando o nome atual da chave
            final String currentProperty = property.substring(0, property.indexOf("."));
            final ProjectionImpl current;
            //já existe esse chave no nível atual?
            if (projection.containsProperty(currentProperty)) {
                //recuperando a chave no nível atual
                current = projection.getByProperty(currentProperty);
            } else {
                //se chegou aqui é sinal que não existe ainda essa propriedade no nível atual
                current = this.createNewSubProjection();
                current.property = currentProperty;
                current.entityMetaData = entityMetaData.getFieldByName(currentProperty);
                projection.addProjection(current);
            }
            current.addProjection(current, current.entityMetaData, property.substring(property.indexOf(".") + 1), criterion);
        } else {
            final ProjectionImpl current = this.createNewSubProjection();
            current.property = property;
            current.entityMetaData = entityMetaData.getFieldByName(property);
            projection.addProjection(current);
            current.addCriterion((CriterionImpl) criterion);
        }
        return projection;
    }

    @Override
    public List<AggregationOperation> getPipeline() {
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
            if (!this.subpropertiesIsEmpty() && this.subproperties.get(0).isId()) {
                list.addAll(this.subproperties.get(0).criterions.stream().map(it -> this.extractOperation(it)).collect(toList()));
            }
            if (!this.subpropertiesIsEmpty() && this.subproperties.size() > 1 && !this.subproperties.get(1).isId()) {
                list.addAll(this.entityMetaData.createProject());
            }
            //final List<AggregationOperation> list = new LinkedList<>(this.entityMetaData.createProject());
            this.generatedLookup = true;//marcando que essa propriedade já foi fieta lookup
            if (!this.subpropertiesIsEmpty()) {
                list.addAll(
                        this.subproperties.stream().map(it -> it.getPipeline()).reduce((acc, others) -> {
                            acc.addAll(others);
                            return acc;
                        }).orElse(null)
                );
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

    private ProjectionImpl addCriterion(final CriterionImpl criterion) {
        if (criterion != null) {
            if (this.criterions == null) {
                this.criterions = new LinkedList<>();
            }
            this.criterions.add(criterion.setOrder(this.generateOrder()).setLevel(this.generateLevel()));
        }
        return this;
    }

    private ProjectionImpl addProjection(final ProjectionImpl projection) {
        if (projection != null) {
            if (this.subproperties == null) {
                this.subproperties = new LinkedList<>();
            }
            this.subproperties.add(projection);
        }
        return this;
    }

    private AggregationOperation extractOperation(final Criterion criterion) {
        return criterion.getOperator().isCriteriaOperation() ?
                match((Criteria) criterion.getOperator().extract(entityMetaData, this.property, criterion.getValue())) :
                (AggregationOperation) criterion.getOperator().extract(entityMetaData, this.property, criterion.getValue());
    }

    /**
     * Retorna um campo com base na chave passada como parametro
     */
    public ProjectionImpl getByProperty(final String key) {
        return this.getByProperty(key, this);
    }

    private ProjectionImpl getByProperty(final String property, final ProjectionImpl projection) {
        //se o nome tiver . é necessário fazezr recursividade1
        if (property.contains(".")) {
            //pegando o primeiro nome
            final String currentProperty = property.substring(0, property.indexOf("."));

            final ProjectionImpl currentProjection;
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
    public boolean containsProperty(final String key) {
        return this.containsProperty(key, this);
    }

    private boolean containsProperty(final String property, final ProjectionImpl projection) {
        //se o nome tiver . é necessário fazezr recursividade
        if (property.contains(".")) {
            //pegando o primeiro nome
            final String currentProperty = property.substring(0, property.indexOf("."));
            if (projection.subpropertiesIsEmpty()) {
                return false;
            }
            final ProjectionImpl currentProjection =
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

    public boolean subpropertiesIsEmpty() {
        return isEmpty(this.subproperties);
    }

    public boolean criterionsIsEmpty() {
        return isEmpty(this.criterions);
    }

    protected boolean isDBRef() {
        return this.entityMetaData != null && this.entityMetaData.isDBRef();
    }

    protected boolean isId() {
        return this.entityMetaData != null && this.entityMetaData.isId();
    }

    /**
     * Cria uma nova instala de {@link ProjectionImpl} mantendo o controle de level
     */
    private ProjectionImpl createNewSubProjection() {
        final ProjectionImpl current = new ProjectionImpl();
        current.levelControl = this.generateLevel();
        current.level = this.generateLevel();
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
