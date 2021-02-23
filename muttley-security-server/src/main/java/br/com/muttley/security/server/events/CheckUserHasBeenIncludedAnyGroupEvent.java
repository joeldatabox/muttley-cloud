package br.com.muttley.security.server.events;

import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira 23/02/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class CheckUserHasBeenIncludedAnyGroupEvent extends ApplicationEvent {
    private boolean userHasBeenIncludedAnyGroup = false;
    private final String userName;

    public CheckUserHasBeenIncludedAnyGroupEvent(final String userName) {
        super(userName);
        this.userName = userName;
    }

    @Override
    public String getSource() {
        return this.userName;
    }

    public boolean isUserHasBeenIncludedAnyGroup() {
        return userHasBeenIncludedAnyGroup;
    }

    public CheckUserHasBeenIncludedAnyGroupEvent setUserHasBeenIncludedAnyGroup(final boolean userHasBeenIncludedAnyGroup) {
        this.userHasBeenIncludedAnyGroup = userHasBeenIncludedAnyGroup;
        return this;
    }
}
