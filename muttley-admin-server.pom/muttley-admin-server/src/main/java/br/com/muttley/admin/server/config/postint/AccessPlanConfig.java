package br.com.muttley.admin.server.config.postint;


import br.com.muttley.model.admin.event.DataBaseHasBeenMigrateEvent;
import br.com.muttley.model.security.AccessPlan;
import br.com.muttley.security.feign.AccessPlanServiceClient;
import br.com.muttley.security.feign.OwnerServiceClient;
import br.com.muttley.security.feign.UserServiceClient;
import br.com.muttley.security.feign.WorkTeamServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * @author Joel Rodrigues Moreira on 22/04/2021
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Realiza a criação dos planos basico de utilização do sistema.
 */
@Component
public class AccessPlanConfig implements ApplicationListener<DataBaseHasBeenMigrateEvent> {
    private final AccessPlanServiceClient accessplanService;
    private final OwnerServiceClient ownerService;
    private final UserServiceClient userService;
    private final WorkTeamServiceClient workTeamService;

    @Autowired
    public AccessPlanConfig(final AccessPlanServiceClient accessplanService, final OwnerServiceClient ownerService, final UserServiceClient userService, final WorkTeamServiceClient workTeamService) {
        this.accessplanService = accessplanService;
        this.ownerService = ownerService;
        this.userService = userService;
        this.workTeamService = workTeamService;
    }

    @Override
    public void onApplicationEvent(final DataBaseHasBeenMigrateEvent event) {
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                }
                accessplanService.count();
            }
        }).start();*/
        if (this.accessplanService.count() == 0) {
            getDefaultPlanos()
                    .forEach(p -> accessplanService.save(p, ""));
        }
    }

    private final Collection<AccessPlan> getDefaultPlanos() {
        return asList(new AccessPlan()

                .setName("Básico")
                .setTotalUsers(10));
    }
}
