package br.com.muttley.security.server.events;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserData;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 11/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class NewUserHasBeenAddedInBaseEvent extends ApplicationEvent {
    private final NewUserHasBeenAddedInBaseItemEvent source;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public NewUserHasBeenAddedInBaseEvent(final NewUserHasBeenAddedInBaseItemEvent source) {
        super(source);
        this.source = source;
    }

    @Override
    public NewUserHasBeenAddedInBaseItemEvent getSource() {
        return this.source;
    }

    @Getter
    public static class NewUserHasBeenAddedInBaseItemEvent {
        private final User currentUser;
        private final UserData newUserAdded;

        public NewUserHasBeenAddedInBaseItemEvent(final User currentUser, final UserData newUserAdded) {
            this.currentUser = currentUser;
            this.newUserAdded = newUserAdded;
        }
    }
}
