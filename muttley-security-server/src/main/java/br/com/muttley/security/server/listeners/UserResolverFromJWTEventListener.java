package br.com.muttley.security.server.listeners;

import br.com.muttley.model.security.events.UserResolverFromJWTEvent;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira 12/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class UserResolverFromJWTEventListener implements ApplicationListener<UserResolverFromJWTEvent> {
    private final UserService service;

    @Autowired
    public UserResolverFromJWTEventListener(final UserService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(final UserResolverFromJWTEvent userResolverFromJWTEvent) {
        userResolverFromJWTEvent.setUserResolved(this.service.getUserFromToken(userResolverFromJWTEvent.getSource()));
    }
}
