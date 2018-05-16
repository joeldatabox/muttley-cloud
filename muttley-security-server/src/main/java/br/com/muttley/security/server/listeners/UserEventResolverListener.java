package br.com.muttley.security.server.listeners;

import br.com.muttley.model.security.jackson.UserEventResolver;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 16/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class UserEventResolverListener implements ApplicationListener<UserEventResolver> {
    private final UserService service;

    @Autowired
    public UserEventResolverListener(final UserService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(final UserEventResolver userEventResolver) {
        userEventResolver.setValueResolved(this.service.findByEmail(userEventResolver.getEmail()));
    }
}
