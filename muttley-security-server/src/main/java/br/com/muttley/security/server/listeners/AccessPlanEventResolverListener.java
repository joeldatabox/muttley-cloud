package br.com.muttley.security.server.listeners;

import br.com.muttley.model.security.events.AccessPlanResolver;
import br.com.muttley.security.server.service.AccessPlanService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 29/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class AccessPlanEventResolverListener implements ApplicationListener<AccessPlanResolver> {
    private final AccessPlanService planoService;

    @Autowired
    public AccessPlanEventResolverListener(final AccessPlanService planoService) {
        this.planoService = planoService;
    }

    @Override
    public void onApplicationEvent(final AccessPlanResolver event) {
        event.setValueResolved(this.planoService.findById(null, new ObjectId(event.getSource())));
    }
}
