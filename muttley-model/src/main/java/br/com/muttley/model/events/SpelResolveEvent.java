package br.com.muttley.model.events;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationEvent;
import org.springframework.expression.Expression;

/**
 * @author Joel Rodrigues Moreira on 08/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class SpelResolveEvent<T> extends ApplicationEvent {
    private final String source;
    @Getter
    @Setter
    @Accessors(chain = true)
    private T resolved;
    @Getter
    @Setter
    @Accessors(chain = true)
    private Expression expression;

    public SpelResolveEvent(final String source) {
        super(source);
        this.source = source;
    }

    @Override
    public String getSource() {
        return source;
    }

    public boolean isResolved() {
        return this.resolved != null;
    }
}
