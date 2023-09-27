package br.com.muttley.model.security.events;

import br.com.muttley.model.security.Password;
import br.com.muttley.model.security.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 28/10/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class SendNewPasswordRecoveredEvent extends ApplicationEvent {
    private final User user;
    @Getter
    private final String hallPassword;

    public SendNewPasswordRecoveredEvent(User source, final String hallPassword) {
        super(source);
        this.user = source;
        this.hallPassword = hallPassword;
    }

    @Override
    public User getSource() {
        return this.user;
    }
}
