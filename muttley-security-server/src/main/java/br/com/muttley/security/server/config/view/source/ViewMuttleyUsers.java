package br.com.muttley.security.server.config.view.source;

import br.com.muttley.mongo.service.config.source.ViewSource;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.conversions.Bson;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Joel Rodrigues Moreira on 29/04/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ViewMuttleyUsers implements ViewSource {
    private final String VERSION = "1.0.7";
    private final String NAME;//= "view_muttley_users";
    private final String SOURCE;//= "muttley-users-base";
    private final String DESCRIPTION = "A view foi criada para facilitar a listagem de usuários e seus owners já linkados";

    public ViewMuttleyUsers(final DocumentNameConfig documentNameConfig) {
        this.NAME = documentNameConfig.getNameViewCollectionUser();
        this.SOURCE = documentNameConfig.getNameCollectionUserBase();
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
         db.getCollection("muttley-users-base").aggregate([
         {$unwind:"$users"},
         //preparando para lookup
         {$project:{owner:1, user:{$objectToArray:"$users.user"}, status:"$users.status"}},
         {$project:{owner:1, user:{$arrayElemAt:["$user.v",1]}, status:1}},
         //lookup comusers
         {$lookup:{
         from:"muttley-users",
         localField:"user",
         foreignField:"_id",
         as:"user"
         }},
         {$unwind:"$user"},
         //removendo o usuário do odin
         {$match:{"user.odinUser":false}},
         //exibindo apenas os dados necessários
         {$project:{_id:"$user._id", _class:"$user._class", name:"$user.name", userName:"$user.userName", email:"$user.email", nickUsers:"$user.nickUsers", owner:1, description:"$user.description", status:1}},
         ])
         */
        return asList(
                new BsonDocument("$unwind", new BsonString("$users")),
                //preparando para lookup
                new BsonDocument("$project",
                        new BsonDocument(
                                asList(
                                        new BsonElement("owner", _TRUE),
                                        new BsonElement("user", new BsonDocument("$objectToArray", new BsonString("$users.user"))),
                                        new BsonElement("status", new BsonString("$users.status"))
                                )
                        )
                ),
                new BsonDocument("$project",
                        new BsonDocument(
                                asList(
                                        new BsonElement("owner", _TRUE),
                                        new BsonElement("user", new BsonDocument("$arrayElemAt", new BsonArray(asList(new BsonString("$user.v"), new BsonInt32(1))))),
                                        new BsonElement("status", _TRUE)
                                )
                        )
                ),
                //lookup comusers
                new BsonDocument("$lookup",
                        new BsonDocument(
                                asList(
                                        new BsonElement("from", new BsonString("muttley-users")),
                                        new BsonElement("localField", new BsonString("user")),
                                        new BsonElement("foreignField", new BsonString("_id")),
                                        new BsonElement("as", new BsonString("user"))
                                )
                        )
                ),
                new BsonDocument("$unwind", new BsonString("$user")),
                //removendo o usuário do odin
                new BsonDocument("$match", new BsonDocument("user.odinUser", new BsonBoolean(false))),
                //exibindo apenas os dados necessários
                new BsonDocument("$project",
                        new BsonDocument(
                                asList(
                                        new BsonElement("_id", new BsonString("$user._id")),
                                        new BsonElement("_class", new BsonString("$user._class")),
                                        new BsonElement("name", new BsonString("$user.name")),
                                        new BsonElement("userName", new BsonString("$user.userName")),
                                        new BsonElement("email", new BsonString("$user.email")),
                                        new BsonElement("nickUsers", new BsonString("$user.nickUsers")),
                                        new BsonElement("owner", _TRUE),
                                        new BsonElement("description", new BsonString("$user.description")),
                                        new BsonElement("status", _TRUE),
                                        new BsonElement("_view_information",
                                                new BsonDocument(
                                                        asList(
                                                                new BsonElement("version", new BsonString(VERSION)),
                                                                new BsonElement("name", new BsonString(NAME)),
                                                                new BsonElement("source", new BsonString(SOURCE))
                                                        )
                                                )
                                        )
                                )
                        )
                )
        );

    }

    public String getDescription() {
        return DESCRIPTION;
    }
}
