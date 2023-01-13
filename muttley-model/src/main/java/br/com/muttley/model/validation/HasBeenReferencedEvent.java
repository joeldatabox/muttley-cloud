package br.com.muttley.model.validation;

import br.com.muttley.model.Model;
import org.springframework.context.ApplicationEvent;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 13/01/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class HasBeenReferencedEvent<T extends Model> extends ApplicationEvent {

    protected final Reference<T> value;
    private List<CollectionReferences> references;

    public HasBeenReferencedEvent(Reference<T> reference) {
        super(reference);
        this.value = reference;

    }

    @Override
    public Reference<T> getSource() {
        return this.value;
    }

    public List<CollectionReferences> getReferences() {
        return this.references;
    }

    public HasBeenReferencedEvent<T> addReference(CollectionReferences references) {
        if (this.references == null) {
            this.references = new ArrayList<>();
        }
        this.references.add(references);
        return this;
    }

    public boolean containsReference() {
        return !CollectionUtils.isEmpty(this.references);
    }
}
