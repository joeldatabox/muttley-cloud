package br.com.muttley.mongo.autoconfig;

import br.com.muttley.model.util.StreamUtils;
import br.com.muttley.mongo.events.VersioningEvent;
import br.com.muttley.mongo.events.VersioningSource;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexOptions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.conversions.Bson;
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

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mongodb.BasicDBObject.parse;
import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * @author Joel Rodrigues Moreira on 28/07/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyConfigVersioning implements ApplicationListener<VersioningEvent> {
    private final String collection;

    private final String NAME_INDEX = "version_unique";
    private final String DEF_INDEX = "{'version' : 1}";

    private final MongoOperations operations;
    protected final Log log = LogFactory.getLog(this.getClass());

    @Autowired
    public MuttleyConfigVersioning(@Value("${muttley.mongodb.versioningCollection:_versioning}") final String collection, final MongoOperations operations) {
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
                    this.operations.getCollection(collection).updateMany(parse(content.getCondictions()), parse(content.getUpdate()));
                });
            });


            //salvando o historico de adaptação
            this.operations.insert(new Versioning(source));
        }
    }

    private MongoCollection<org.bson.Document> getVersioningCollection() {
        return this.operations.getCollection("_versioning");
    }

    /**
     * Verifica se na collection já contem o index necessário
     * caso não exista o mesmo será criado
     */
    private void checkIndex() {

        final Set<String> indexies = StreamUtils.of(this.getVersioningCollection()
                .listIndexes()
                .iterator())
                .map(index -> index.get("name"))
                .map(Object::toString)
                .collect(Collectors.toSet());
        if (!indexies.contains("version_unique")) {
            final Bson indexDefinition = parse(DEF_INDEX);
            final IndexOptions options = new IndexOptions().unique(true);
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
