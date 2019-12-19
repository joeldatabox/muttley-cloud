package br.com.muttley.model.security.events;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.WorkTeam;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Evento disparado toda vez que se tenta inserir um novo grupo de trabalho
 * O mesmo se faz necessário para não haver vazamento de segurança.
 * <p>
 * Por padrão só iremos aceitar owner caso arequisição venha do servidor odin,
 * caso contrario pegaremos o owner da requisição corrente
 *
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ValidateOwnerInWorkGroupEvent extends ApplicationEvent {
    private final WorkTeam workTeam;
    @Getter
    private final User currenteUserFromRequest;

    public ValidateOwnerInWorkGroupEvent(final User currenteUserFromRequest, final WorkTeam workTeam) {
        super(workTeam);
        this.currenteUserFromRequest = currenteUserFromRequest;
        this.workTeam = workTeam;
    }

    @Override
    public WorkTeam getSource() {
        return this.workTeam;
    }
}
