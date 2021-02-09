package br.com.muttley.model.security.events;

import br.com.muttley.model.security.UserDataBinding;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira 09/02/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserDatabindingValidateMergeEvent extends ApplicationEvent {

    public UserDatabindingValidateMergeEvent(final UserDataBinding source) {
        super(source);
    }

    @Override
    public UserDataBinding getSource() {
        return (UserDataBinding) super.getSource();
    }
}
