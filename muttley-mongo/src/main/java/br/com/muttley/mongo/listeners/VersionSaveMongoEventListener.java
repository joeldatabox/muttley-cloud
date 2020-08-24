package br.com.muttley.mongo.listeners;

import br.com.muttley.metadata.anotations.Version;
import br.com.muttley.mongo.properties.MuttleyMongoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;

/**
 * @author Joel Rodrigues Moreira on 19/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class VersionSaveMongoEventListener extends AbstractMongoEventListener<Object> {
    private final MuttleyMongoProperties properties;

    @Autowired
    public VersionSaveMongoEventListener(MuttleyMongoProperties properties) {
        this.properties = properties;
    }

    @Override
    public void onBeforeSave(BeforeSaveEvent<Object> event) {
        if (properties.isDocumentVersioning()) {
            final Class clazz = event.getSource().getClass();
            if (clazz.isAnnotationPresent(Version.class)) {
                final Version version = (Version) clazz.getAnnotation(Version.class);
                event.getDocument().append(version.field(), version.value());
            } else {
                if (clazz.isAnnotationPresent(Document.class)) {
                    event.getDocument().append("_version", "1");
                }
            }
        }
    }
}
