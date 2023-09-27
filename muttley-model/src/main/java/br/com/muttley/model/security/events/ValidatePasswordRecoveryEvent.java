package br.com.muttley.model.security.events;

import br.com.muttley.model.security.RecoveryPayload;
import org.springframework.context.ApplicationEvent;

import java.util.Objects;

/**
 * @author Joel Rodrigues Moreira on 27/10/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ValidatePasswordRecoveryEvent extends ApplicationEvent {
    private final RecoveryPayload payload;

    public ValidatePasswordRecoveryEvent(final RecoveryPayload payload) {
        super(payload);
        this.payload = payload;

    }

    @Override
    public RecoveryPayload getSource() {
        return this.payload;
    }


    public boolean numberIsValid() {
        return Objects.equals(this.payload.getSeedVerification(), this.payload.getCodeVerification());
    }
}
