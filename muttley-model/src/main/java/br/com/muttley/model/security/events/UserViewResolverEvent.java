package br.com.muttley.model.security.events;

import br.com.muttley.model.security.UserView;
import org.springframework.context.ApplicationEvent;


public class UserViewResolverEvent extends ApplicationEvent {
    final String userName;
    protected UserView valueResolved;

    public UserViewResolverEvent(final String userName) {
        super(userName);
        this.userName = userName;
    }

    public UserView getUserResolver() {
        return valueResolved;
    }

    public UserViewResolverEvent setValueResolved(final UserView valueResolved) {
        this.valueResolved = valueResolved;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isResolved() {
        return this.valueResolved != null;
    }
}
