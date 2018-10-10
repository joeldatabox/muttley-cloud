package br.com.muttley.model.events;

import br.com.muttley.model.security.WorkTeam;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 16/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class WorkTeamCreateEvent extends ApplicationEvent {
    private final WorkTeam workTeam;

    public WorkTeamCreateEvent(final WorkTeam workTeam) {
        super(workTeam);
        this.workTeam = workTeam;
    }

    @Override
    public WorkTeam getSource() {
        return this.workTeam;
    }
}
