package br.com.muttley.security.server.events;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 30/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Evento disparado para recuperação de roles de um determinado usuário
 */
public class CurrentAdminRolesResolverEvent extends ApplicationEvent {
    private final CurrentAdminRolesResolverEventItem source;

    @Getter
    @Setter
    @Accessors(chain = true)
    private boolean resolved = false;

    @Getter
    @Setter
    @Accessors(chain = true)
    private Set<Role> roles;

    public CurrentAdminRolesResolverEvent(final CurrentAdminRolesResolverEventItem source) {
        super(source);
        this.source = source;
    }

    @Override
    public CurrentAdminRolesResolverEventItem getSource() {
        return this.source;
    }

    @Getter
    public static class CurrentAdminRolesResolverEventItem {
        private final JwtToken token;
        private final User user;

        public CurrentAdminRolesResolverEventItem(final JwtToken token, final User user) {
            this.token = token;
            this.user = user;
        }
    }
}
