package br.com.muttley.security.server.listeners;

import br.com.muttley.model.security.event.AccessPlanResolver;
import br.com.muttley.security.server.service.AccessPlanService;
import br.com.muttley.security.infra.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 29/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project agrifocus-cloud
 */
@Component
public class AccessPlanEventResolverListener implements ApplicationListener<AccessPlanResolver> {
    private final UserService userService;
    private final AccessPlanService planoService;

    @Autowired
    public AccessPlanEventResolverListener(final UserService userService, final AccessPlanService planoService) {
        this.userService = userService;
        this.planoService = planoService;
    }

    @Override
    public void onApplicationEvent(final AccessPlanResolver event) {
        event.setValueResolved(this.planoService.findById(userService.getCurrentUser(), new ObjectId(event.getId())));
    }
}
