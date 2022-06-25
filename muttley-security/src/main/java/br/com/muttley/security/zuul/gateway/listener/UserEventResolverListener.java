package br.com.muttley.security.zuul.gateway.listener;

import br.com.muttley.model.security.events.UserResolverEvent;
import br.com.muttley.security.feign.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 04/04/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class UserEventResolverListener implements ApplicationListener<UserResolverEvent> {
    private final UserServiceClient service;

    @Autowired
    public UserEventResolverListener(final UserServiceClient service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(final UserResolverEvent userEventResolver) {
        userEventResolver.setValueResolved(this.service.findByUserName(userEventResolver.getUserName()));
    }
}
