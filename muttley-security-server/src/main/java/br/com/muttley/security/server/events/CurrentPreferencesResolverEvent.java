package br.com.muttley.security.server.events;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.UserPreferences;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira 29/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Evento disparado para recuperação de preferencias de um determinado usuário
 */
public class CurrentPreferencesResolverEvent extends ApplicationEvent {

    private final CurrentPreferencesResolverEventItem item;
    @Getter
    @Setter
    @Accessors(chain = true)
    private boolean resolved = false;

    @Getter
    @Setter
    @Accessors(chain = true)
    private UserPreferences userPreferences;

    public CurrentPreferencesResolverEvent(final CurrentPreferencesResolverEventItem item) {
        super(item);
        this.item = item;
    }

    @Override
    public CurrentPreferencesResolverEventItem getSource() {
        return this.item;
    }

    @Getter
    public static class CurrentPreferencesResolverEventItem {
        private final JwtToken token;
        private final User user;

        public CurrentPreferencesResolverEventItem(final JwtToken token, final User user) {
            this.token = token;
            this.user = user;
        }
    }
}
