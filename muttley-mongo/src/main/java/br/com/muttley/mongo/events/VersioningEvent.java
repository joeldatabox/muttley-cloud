package br.com.muttley.mongo.events;

import org.springframework.context.ApplicationEvent;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 28/07/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class VersioningEvent extends ApplicationEvent {
    private final Set<VersioningSource> source;

    public VersioningEvent(final Set<VersioningSource> source) {
        super(source);
        this.source = source;
    }

    @Override
    public Set<VersioningSource> getSource() {
        return this.source;
    }
}
