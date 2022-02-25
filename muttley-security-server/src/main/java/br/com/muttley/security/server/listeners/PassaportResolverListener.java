package br.com.muttley.security.server.listeners;

import br.com.muttley.domain.service.listener.AbstractModelResolverEventListener;
import br.com.muttley.model.security.Passaport;
import br.com.muttley.model.security.events.PassaportResolverEvent;
import br.com.muttley.security.server.service.AuthService;
import br.com.muttley.security.server.service.PassaportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 07/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class PassaportResolverListener extends AbstractModelResolverEventListener<Passaport, PassaportResolverEvent> {
    private final AuthService authService;
    private final PassaportService passaportService;

    @Autowired
    public PassaportResolverListener(AuthService authService, PassaportService passaportService) {
        this.authService = authService;
        this.passaportService = passaportService;
    }

    @Override
    protected Passaport loadValueById(String id) {
        return passaportService.findById(authService.getCurrentJwtUser().getOriginUser(), id);
    }
}
