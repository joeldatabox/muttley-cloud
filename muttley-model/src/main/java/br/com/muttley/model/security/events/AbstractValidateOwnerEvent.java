package br.com.muttley.model.security.events;

import br.com.muttley.model.security.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Evento disparado toda vez que se tenta inserir um novo registro
 * O mesmo se faz necessário para não haver vazamento de segurança.
 *
 * @author Joel Rodrigues Moreira on 08/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class AbstractValidateOwnerEvent<T> extends ApplicationEvent {
    @Getter
    private final User currenteUserFromRequest;
    private final T value;

    public AbstractValidateOwnerEvent(final User currenteUserFromRequest, final T source) {
        super(source);
        this.value = source;
        this.currenteUserFromRequest = currenteUserFromRequest;
    }

    @Override
    public T getSource() {
        return this.value;
    }
}
