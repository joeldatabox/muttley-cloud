package br.com.muttley.security.server.events;

import br.com.muttley.model.security.AccessPlan;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 21/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class AccessPlanDefaultEvent extends ApplicationEvent {

    @Getter
    @Setter
    @Accessors(chain = true)
    private AccessPlan resolved;

    public AccessPlanDefaultEvent(final String source) {
        super(source);
    }


}
