package br.com.muttley.mongo.service.listeners;

import br.com.muttley.mongo.service.infra.migration.MuttleyMigrationEvent;
import br.com.muttley.mongo.service.infra.migration.MuttleyMigrationModel;
import br.com.muttley.mongo.service.infra.migration.MuttleyMigrationSource;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.muttley.mongo.service.infra.migration.MuttleyMigrationModel.COLLECTION;
import static br.com.muttley.mongo.service.infra.migration.MuttleyMigrationModel.NAME_INDEX;
import static com.mongodb.BasicDBObject.parse;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 11/02/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class MuttleyMigrationListener implements ApplicationListener<ApplicationReadyEvent> {

    protected final ApplicationEventPublisher publisher;
    protected final MongoOperations operations;
    protected final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    public MuttleyMigrationListener(final ApplicationEventPublisher publisher, final MongoOperations operations) {
        this.publisher = publisher;
        this.operations = operations;
    }


    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        final MuttleyMigrationEvent migrationEvent = new MuttleyMigrationEvent();
        //disparando o evento para capiturar itens para migração
        this.publisher.publishEvent(migrationEvent);

        if (migrationEvent.containsMigrations()) {

            //criando o index de versionamento caso necessário
            this.checkIndex();

            migrationEvent.getMigrationSources()
                    .stream()
                    .sorted((source, otherSource) -> source.getVersion() > otherSource.getVersion() ? 1 : -1)
                    .forEach(it -> {
                        this.processMigration(it);
                    });
        }
    }

    private void processMigration(final MuttleyMigrationSource source) {
        final MuttleyMigrationModel result = this.operations.findOne(getFilterVersion(source.getVersion()), MuttleyMigrationModel.class);

        if (result == null) {
            //esse versionamento ainda não foi aplicado, logo devemos aplicar
            source.getContents().stream().forEach(content -> {
                //percorrendo todas as collections necessárias
                Stream.of(content.onCollections()).forEach(collection -> {
                    //aplicando os ajustes na collection
                    this.operations.getCollection(collection).updateMulti(parse(content.getCondictions()), parse(content.getUpdate()));
                });
            });


            //salvando o historico de adaptação
            this.operations.insert(new MuttleyMigrationModel(source));
        }
    }

    private DBCollection getVersioningCollection() {
        return this.operations.getCollection(COLLECTION);
    }

    /**
     * Verifica se na collection já contem o index necessário
     * caso não exista o mesmo será criado
     */
    private void checkIndex() {


        final Set<String> indexies = this.getVersioningCollection().getIndexInfo()
                .stream()
                .map(index -> index.get("name"))
                .map(Object::toString)
                .collect(Collectors.toSet());
        if (!indexies.contains(NAME_INDEX)) {
            final DBObject indexDefinition = parse(MuttleyMigrationModel.DEF_INDEX);
            //final DBObject options = new BasicDBObject("unique", 1);
            this.operations.getCollection(COLLECTION).createIndex(indexDefinition, NAME_INDEX, true);
            log.info("Created index \"" + NAME_INDEX + "\" for collection \"" + COLLECTION + "\"");
        } else {
            log.info("The index \"" + NAME_INDEX + "\" already exists for collection \"" + COLLECTION + "\"");
        }

    }

    private Query getFilterVersion(final long version) {
        return new Query(where("version").is(version));
    }
}
