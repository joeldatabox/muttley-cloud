package br.com.muttley.security.server.listeners;

import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.security.server.events.OwnerCreateEvent;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 16/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Ao se criar um Owner no sistema, devemos garantir que seja criado um Grupo de trabalho
 * padrão para esse mesmo usuário
 */
@Component
public class OwnerCreateEventListener implements ApplicationListener<OwnerCreateEvent> {

    private final WorkTeamService service;

    @Autowired
    public OwnerCreateEventListener(final WorkTeamService service) {
        this.service = service;
    }

    @Override
    public void onApplicationEvent(final OwnerCreateEvent ownerCreateEvent) {
        this.service.save(
                ownerCreateEvent.getSource().getUserMaster(),
                new WorkTeam()
                        .setName("Master")
                        .setDescription("Esse é o grupo principal")
                        .setOwner(ownerCreateEvent.getSource())
                        .setUserMaster(ownerCreateEvent.getSource().getUserMaster())
                        .addMember(ownerCreateEvent.getSource().getUserMaster())
        );
    }
}
