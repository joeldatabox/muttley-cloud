package br.com.muttley.mongo.service.infra.migration;

import org.springframework.context.ApplicationEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 10/02/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyMigrationEvent extends ApplicationEvent {
    private Set<MuttleyMigrationSource> migrationSources;

    public MuttleyMigrationEvent() {
        super("");
        this.migrationSources = new HashSet<>();
    }

    @Override
    public Set<MuttleyMigrationSource> getSource() {
        return this.migrationSources;
    }

    public Set<MuttleyMigrationSource> getMigrationSources() {
        return migrationSources;
    }

    public MuttleyMigrationEvent addMigrations(final MuttleyMigrationSource... source) {
        this.addMigrations(asList(source));
        return this;
    }

    public MuttleyMigrationEvent addMigrations(final Collection<? extends MuttleyMigrationSource> migrationSources) {
        this.migrationSources.addAll(migrationSources);
        return this;
    }

    public boolean containsMigrations() {
        return !isEmpty(this.migrationSources);
    }
}
