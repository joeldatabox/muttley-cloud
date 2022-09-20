package br.com.muttley.security.server.events;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserDataBinding;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira 30/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Evento utilizado para carregamento de informações de databinding do banco de dados
 */
public class CurrentDatabindingResolverEvent extends ApplicationEvent {
    private final CurrentDatabindingEventItem source;

    @Getter
    @Setter
    @Accessors(chain = true)
    private boolean resolved = false;

    @Getter
    @Setter
    @Accessors(chain = true)
    private List<UserDataBinding> dataBindings;

    public CurrentDatabindingResolverEvent(final CurrentDatabindingEventItem source) {
        super(source);
        this.source = source;
    }

    @Override
    public CurrentDatabindingEventItem getSource() {
        return this.source;
    }

    @Getter
    public static class CurrentDatabindingEventItem<T> {
        private final T token;
        private final User user;

        public CurrentDatabindingEventItem(final T token, final User user) {
            this.token = token;
            this.user = user;
        }
    }
}
