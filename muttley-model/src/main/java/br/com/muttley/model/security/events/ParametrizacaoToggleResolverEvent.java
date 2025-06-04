package br.com.muttley.model.security.events;

import br.com.muttley.model.parametrizacao.ParametrizacaoToggle;
import br.com.muttley.model.parametrizacao.Parametro;
import org.springframework.context.ApplicationEvent;

public class ParametrizacaoToggleResolverEvent extends ApplicationEvent {

    final String id;
    protected ParametrizacaoToggle valueResolved;

    public ParametrizacaoToggleResolverEvent(String id) {
        super(id);
        this.id = id;
    }

    public ParametrizacaoToggle getUserResolver() {
        return valueResolved;
    }

    public ParametrizacaoToggleResolverEvent setValueResolved(final ParametrizacaoToggle valueResolved) {
        this.valueResolved = valueResolved;
        return this;
    }

    public String getId() {
        return id;
    }

    public boolean isResolved() {
        return this.valueResolved != null;
    }
}
