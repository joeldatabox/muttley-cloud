package br.com.muttley.model.security.events;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.UserPreferences;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 25/03/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Evento é disparado toda vez que se carrega as preferencias do usuário logado
 * O ouvinte será responsável por carregar detalhes da mesma caso necessário como
 * por exemplo o owner e tudo mais
 */
public class DeserializeUserPreferencesEvent extends ApplicationEvent {
    public DeserializeUserPreferencesEvent(final UserPreferencesResolverEventItem preferences) {
        super(preferences);
    }

    @Override
    public UserPreferencesResolverEventItem getSource() {
        return (UserPreferencesResolverEventItem) super.getSource();
    }

    public User getUser() {
        return this.getSource().getUser();
    }

    public UserPreferences getPreferences() {
        return this.getSource().getUserPreferences();
    }

    @Getter
    public static class UserPreferencesResolverEventItem {
        private final User user;
        private final UserPreferences userPreferences;

        public UserPreferencesResolverEventItem(User user, UserPreferences userPreferences) {
            this.user = user;
            this.userPreferences = userPreferences;
        }
    }
}
