package br.com.muttley.model.security.events;

import br.com.muttley.model.security.KeyUserDataBinding;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira 10/02/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class KeyUserDataBindingResolverEvent extends ApplicationEvent {
    private KeyUserDataBinding resolvedValue;

    public KeyUserDataBindingResolverEvent(final String key) {
        super(key);
    }

    @Override
    public String getSource() {
        return (String) super.getSource();
    }

    public KeyUserDataBinding getResolvedValue() {
        return resolvedValue;
    }

    public KeyUserDataBindingResolverEvent setResolvedValue(final KeyUserDataBinding resolvedValue) {
        this.resolvedValue = resolvedValue;
        return this;
    }

    public boolean isResolved() {
        return resolvedValue != null;
    }
}
