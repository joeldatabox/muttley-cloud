package br.com.muttley.mongo.infra.newagregation.projections;

import br.com.muttley.mongo.infra.newagregation.operators.Operator2;
import lombok.Getter;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 10/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
public class Criterion3Impl implements Criterion3 {
    private Operator2 operator;
    private String key;
    private Object value;
    private List<Criterion3> subcriterions;
    private final ProjectionMetadata metadata;

    protected Criterion3Impl(final ProjectionMetadata metadata) {
        this.subcriterions = new LinkedList<>();
        this.metadata = metadata;
    }

    protected Criterion3Impl(final ProjectionMetadata metadata, final Operator2 operator, final String key, final Object value, final List<Criterion3> subcriterions) {
        this(metadata);
        this.operator = operator;
        this.key = key;
        this.value = value;
        this.addSubcriterions(subcriterions);
    }

    public Criterion3Impl(final ProjectionMetadata metadata, final Operator2 operator, final List<Criterion3> subCriterions) {
        this(metadata);
        this.operator = operator;
        this.addSubcriterions(subCriterions);
    }

    protected Criterion3Impl addSubcriterions(final Criterion3 criterion3) {
        if (criterion3 != null) {
            this.subcriterions.add(criterion3);
        }
        return this;
    }

    protected Criterion3Impl addSubcriterions(final Collection<Criterion3> criterion3) {
        if (!CollectionUtils.isEmpty(criterion3)) {
            criterion3.forEach(it -> this.addSubcriterions(it));
        }
        return this;
    }
}
