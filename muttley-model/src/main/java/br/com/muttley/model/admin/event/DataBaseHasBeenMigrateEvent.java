package br.com.muttley.model.admin.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 06/09/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class DataBaseHasBeenMigrateEvent extends ApplicationEvent {
    public DataBaseHasBeenMigrateEvent() {
        super("HasBeenMigrateEvent");
    }
}
