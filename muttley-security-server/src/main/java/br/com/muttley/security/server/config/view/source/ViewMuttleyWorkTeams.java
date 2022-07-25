package br.com.muttley.security.server.config.view.source;

import br.com.muttley.mongo.service.config.source.ViewSource;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 25/07/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ViewMuttleyWorkTeams implements ViewSource {
    private final String VERSION = "1.0.0";
    private final String NAME;
    private final String SOURCE;
    private final String DESCRIPTION = "A view foi criada para facilitar o processo de listagem de workteams por userMaster. O operador $graphLookup não aceita trabalhar com array, logo, se faz necessário dar um $unwind para facilitar o processo";

    public ViewMuttleyWorkTeams(final DocumentNameConfig documentNameConfig) {
        this.NAME = documentNameConfig.getNameViewCollectionPassaport();
        this.SOURCE = documentNameConfig.getNameCollectionPassaport();
    }


    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String getViewName() {
        return NAME;
    }

    @Override
    public String getViewOn() {
        return SOURCE;
    }

    @Override
    public List<? extends Bson> getPipeline() {
        /**
         * db.getCollection("muttley-work-teams").aggregate([
         *     {$unwind:"$usersMaster"}
         * ])
         */
        return Arrays.asList(
                new BsonDocument("$unwind", new BsonString("$usersMaster"))
        );
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
