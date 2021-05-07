package br.com.muttley.security.server.listeners;

import br.com.muttley.domain.service.listener.AbstractModelResolverEventListener;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.model.security.events.WorkTeamResolverEvent;
import br.com.muttley.security.server.service.AuthService;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 07/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class WorkTeamResolverListener extends AbstractModelResolverEventListener<WorkTeam, WorkTeamResolverEvent> {
    private final AuthService authService;
    private final WorkTeamService workTeamService;

    @Autowired
    public WorkTeamResolverListener(AuthService authService, WorkTeamService workTeamService) {
        this.authService = authService;
        this.workTeamService = workTeamService;
    }

    @Override
    protected WorkTeam loadValueById(String id) {
        return workTeamService.findById(authService.getCurrentJwtUser().getOriginUser(), id);
    }
}
