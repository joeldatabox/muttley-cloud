package br.com.muttley.mongo.query.projections;

import br.com.muttley.mongo.infra.metadata.EntityMetaData;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;

import java.util.LinkedList;
import java.util.List;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 02/09/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ProjectionImpl implements Projection {
    private int level = 0;
    private int ordem = 0;
    private EntityMetaData entityMetaData;
    private String property;
    private List<ProjectionImpl> subproperties;
    private List<Criterion> criterions;

    protected ProjectionImpl() {
    }

    @Override
    public Projection addProjection(final Projection projection, final EntityMetaData entityMetaData, final String property, final Criterion criterion) {
        return this.addProjection((ProjectionImpl) projection, entityMetaData, property, criterion);
    }

    private Projection addProjection(final ProjectionImpl projection, final EntityMetaData entityMetaData, final String property, final Criterion criterion) {
        if (projection.containsProperty(property)) {
            //adiciona os demais criterios aqui
            projection.addCriterion(criterion);
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
                current = new ProjectionImpl();
                current.property = currentProperty;
                current.entityMetaData = entityMetaData.getFieldByName(currentProperty);
                projection.addProjection(current);
            }
            current.addProjection(current, current.entityMetaData, property.substring(property.indexOf(".") + 1), criterion);
        } else {
            final ProjectionImpl current = new ProjectionImpl();
            current.property = property;
            current.entityMetaData = entityMetaData.getFieldByName(property);
            projection.addProjection(current);
            current.addCriterion(criterion);
        }
        return projection;
    }

    @Override
    public List<AggregationOperation> getPipeline() {
        return null;
    }

    private ProjectionImpl addCriterion(final Criterion criterion) {
        if (criterion != null) {
            if (this.criterions == null) {
                this.criterions = new LinkedList<>();
            }
            this.criterions.add(criterion);
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


}
