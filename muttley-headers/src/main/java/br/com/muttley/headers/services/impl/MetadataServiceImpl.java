package br.com.muttley.headers.services.impl;

import br.com.muttley.headers.components.MuttleyCurrentTimezone;
import br.com.muttley.headers.components.MuttleyCurrentVersion;
import br.com.muttley.headers.components.MuttleyUserAgentName;
import br.com.muttley.headers.services.MetadataService;
import br.com.muttley.model.Document;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.VersionDocument;
import br.com.muttley.model.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author Joel Rodrigues Moreira 12/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class MetadataServiceImpl implements MetadataService {
    @Autowired
    protected MuttleyCurrentTimezone currentTimezone;
    @Autowired
    protected MuttleyCurrentVersion currentVersion;
    @Autowired
    protected MuttleyUserAgentName userAgentName;

    @Override
    public void generateNewMetadataFor(final User user, final Document value) {
        //se não tiver nenhum metadata criado, vamos criar um
        if (!value.containsMetadata()) {
            value.setMetadata(new MetadataDocument()
                    .setTimeZones(this.currentTimezone.getCurrentTimezoneDocument())
                    .setVersionDocument(
                            new VersionDocument()
                                    .setOriginVersionClientCreate(this.currentVersion.getCurrentValue())
                                    .setOriginVersionClientLastUpdate(this.currentVersion.getCurrentValue())
                                    .setOriginNameClientCreate(this.userAgentName.getCurrentValue())
                                    .setOriginNameClientLastUpdate(this.userAgentName.getCurrentValue())
                                    .setServerVersionCreate(this.currentVersion.getCurrenteFromServer())
                                    .setServerVersionLastUpdate(this.currentVersion.getCurrenteFromServer())
                    ));
        } else {
            //se não tem um timezone válido, vamos criar um
            if (!value.getMetadata().containsTimeZones()) {
                value.getMetadata().setTimeZones(this.currentTimezone.getCurrentTimezoneDocument());
            } else {
                //se chegou aqui é sinal que já possui infos de timezones e devemos apenas checar e atualizar caso necessário

                //O timezone atual informado é valido?
                if (value.getMetadata().getTimeZones().isValidCurrentTimeZone()) {
                    //adicionado a mesma info no createTimezone já que estamos criando um novo registro
                    value.getMetadata().getTimeZones().setCreateTimeZone(value.getMetadata().getTimeZones().getCurrentTimeZone());
                }

                //adicionando infos de timezone do servidor
                final String currentServerTimezone = this.currentTimezone.getCurrenteTimeZoneFromServer();
                value.getMetadata().getTimeZones().setServerCreteTimeZone(currentServerTimezone);
                value.getMetadata().getTimeZones().setServerCurrentTimeZone(currentServerTimezone);
            }

            //criando version valido
            value.getMetadata().setVersionDocument(
                    new VersionDocument()
                            .setOriginVersionClientCreate(this.currentVersion.getCurrentValue())
                            .setOriginVersionClientLastUpdate(this.currentVersion.getCurrentValue())
                            .setOriginNameClientCreate(this.userAgentName.getCurrentValue())
                            .setOriginNameClientLastUpdate(this.userAgentName.getCurrentValue())
                            .setServerVersionCreate(this.currentVersion.getCurrenteFromServer())
                            .setServerVersionLastUpdate(this.currentVersion.getCurrenteFromServer())
            );


        }
    }

    @Override
    public void generateNewMetadataFor(final User user, final Collection<Document> values) {
        values.forEach(it -> {
            this.generateNewMetadataFor(user, it);
        });
    }

    @Override
    public void generateMetaDataUpdateFor(final User user, final MetadataDocument currentMetadata, final Document value) {
        currentMetadata
                .getTimeZones()
                .setServerCurrentTimeZone(
                        this.currentTimezone.getCurrenteTimeZoneFromServer()
                );


        //se veio informações no registro, devemos aproveitar
        if (value.containsMetadata()) {
            if (value.getMetadata().containsTimeZones()) {
                if (value.getMetadata().getTimeZones().isValidCurrentTimeZone()) {
                    currentMetadata.getTimeZones().setCurrentTimeZone(value.getMetadata().getTimeZones().getCurrentTimeZone());
                } else {
                    currentMetadata.getTimeZones().setCurrentTimeZone(this.currentTimezone.getCurrentValue());
                }
            } else {
                currentMetadata.getTimeZones().setCurrentTimeZone(this.currentTimezone.getCurrentValue());
            }
        } else {
            currentMetadata.getTimeZones()
                    .setCurrentTimeZone(this.currentTimezone.getCurrentValue())
                    .setServerCurrentTimeZone(this.currentTimezone.getCurrenteTimeZoneFromServer());
        }
        //setando versionamento
        currentMetadata
                .getVersionDocument()
                .setServerVersionLastUpdate(this.currentVersion.getCurrenteFromServer())
                .setOriginNameClientLastUpdate(this.userAgentName.getCurrentValue())
                .setOriginVersionClientLastUpdate(this.currentVersion.getCurrentValue());

        value.setMetadata(currentMetadata);
    }
}
