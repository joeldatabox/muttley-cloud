package br.com.muttley.mongo.service.config;

import br.com.muttley.mongo.service.infra.VersioningEvent;
import br.com.muttley.mongo.service.infra.VersioningSource;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.BasicDBObject.parse;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 10/02/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class ConfigVersioning implements ApplicationListener<VersioningEvent> {
    private final String collection;

    private final String NAME_INDEX = "version_unique";
    private final String DEF_INDEX = "{'version' : 1}";

    private final MongoOperations operations;
    protected final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    public ConfigVersioning(@Value("${muttley.mongodb.versioningCollection:_versioning}") final String collection, final MongoOperations operations) {
        this.collection = collection;
        this.operations = operations;
    }

    @Override
    public void onApplicationEvent(final VersioningEvent event) {
        //criando o index de versionamento caso necessário
        this.checkIndex();

        event.getSource().stream().sorted((source, otherSource) -> source.getVersion() > otherSource.getVersion() ? -1 : 1)
                .forEach(it -> {
                    this.tes(it);
                });
    }

    private void tes(final VersioningSource source) {
        final Versioning result = this.operations.findOne(getFilterVersion(source.getVersion()), Versioning.class);

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
            this.operations.insert(new Versioning(source));
        }
    }

    private DBCollection getVersioningCollection() {
        return this.operations.getCollection("_versioning");
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
        if (!indexies.contains("version_unique")) {
            final DBObject indexDefinition = parse(DEF_INDEX);
            final DBObject options = new BasicDBObject("unique", 1);
            this.operations.getCollection("_versioning").createIndex(indexDefinition, options);
            log.info("Created index \"" + NAME_INDEX + "\" for collection \"" + collection + "\"");
        } else {
            log.info("The index \"" + NAME_INDEX + "\" already exists for collection \"" + collection + "\"");
        }

    }

    private Query getFilterVersion(final long version) {
        return new Query(where("version").is(version));
    }

    @Document(collection = "_versioning")
    @CompoundIndexes({
            @CompoundIndex(name = NAME_INDEX, def = DEF_INDEX, unique = true)
    })
    @TypeAlias("versioning")
    public class Versioning {
        @Id
        private String id;
        private long version;
        private Date updatedIn;
        private String description;

        public Versioning(final VersioningSource source) {
            this.version = source.getVersion();
            this.description = source.getDescription();
            this.updatedIn = new Date();
        }

        public Versioning() {
        }

        public String getId() {
            return id;
        }

        public Versioning setId(final String id) {
            this.id = id;
            return this;
        }

        public long getVersion() {
            return version;
        }

        public Versioning setVersion(final long version) {
            this.version = version;
            return this;
        }

        public Date getUpdatedIn() {
            return updatedIn;
        }

        public Versioning setUpdatedIn(final Date updatedIn) {
            this.updatedIn = updatedIn;
            return this;
        }

        public String getDescription() {
            return description;
        }

        public Versioning setDescription(final String description) {
            this.description = description;
            return this;
        }
    }
}
