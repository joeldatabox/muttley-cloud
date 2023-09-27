package br.com.muttley.model.security.events;

import br.com.muttley.model.security.UserPayLoad;
import org.springframework.context.ApplicationEvent;
import org.springframework.util.ObjectUtils;

import java.util.Objects;
import java.util.Random;

/**
 * @author Joel Rodrigues Moreira on 29/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Evento criado para implementar lógica de validação de numero telefonico
 */
public class ValidadeUserForeCreateEvent extends ApplicationEvent {
    private final UserPayLoad userPayLoad;

    public ValidadeUserForeCreateEvent(final UserPayLoad userPayLoad) {
        super(userPayLoad);
        this.userPayLoad = userPayLoad;

    }

    @Override
    public UserPayLoad getSource() {
        return this.userPayLoad;
    }


    public boolean numberIsValid() {
        return Objects.equals(this.userPayLoad.getSeedVerification(), this.userPayLoad.getCodeVerification());
    }
}
