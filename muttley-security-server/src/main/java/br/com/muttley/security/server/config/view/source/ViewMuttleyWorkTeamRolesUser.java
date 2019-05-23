package br.com.muttley.security.server.config.view.source;

import br.com.muttley.mongo.service.config.source.ViewSource;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.conversions.Bson;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Joel Rodrigues Moreira on 22/05/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ViewMuttleyWorkTeamRolesUser implements ViewSource {
    private final String VERSION = "1.0.0";
    private final String NAME = "view_muttley_work_teams_roles_user";
    private final String SOURCE = "muttley-work-teams";
    private final String DESCRIPTION = "A view foi criada para facilitar a listagem de usuário por owners juntamente com sua respectivas autorizações";

    @Override
    public String getVersion() {
        return this.VERSION;
    }

    @Override
    public String getViewName() {
        return this.NAME;
    }

    @Override
    public String getViewOn() {
        return this.SOURCE;
    }

    @Override
    public List<? extends Bson> getPipeline() {
        /**
         db.getCollection("muttley-work-teams").aggregate([
         //pegando os dados necessários
         {$project:{ _id:1, owner:1, userMaster:1, members:1, roles:1}},
         {$unwind:'$members'},
         {$unwind:'$roles'},
         {$project:{ _id:1, owner:1, users:["$userMaster", "$members"], roles:1}},
         {$unwind:'$users'},
         {$group:{
         _id:{_id:"$_id", owner:"$owner", user:"$users" },
         roles:{$addToSet:"$roles"}
         }},
         {$project:{ _id: {$arrayElemAt:[{$objectToArray:"$_id.user"},1]}, owner:"$_id.owner", user:"$_id.user", roles:1}},
         {$project:{_id:"$_id.v", userId:"$_id.v", owner:1, user:1, roles:1 }},
         ])
         */

        return asList(
                //pegando os dados necessários
                new BsonDocument("$project",
                        new BsonDocument(
                                asList(
                                        new BsonElement("_id", _TRUE),
                                        new BsonElement("owner", _TRUE),
                                        new BsonElement("userMaster", _TRUE),
                                        new BsonElement("members", _TRUE),
                                        new BsonElement("roles", _TRUE)
                                )
                        )
                ),
                //transformando o array
                new BsonDocument("$unwind", new BsonString("$members")),
                new BsonDocument("$unwind", new BsonString("$roles")),
                //transformando o array
                new BsonDocument("$project",
                        new BsonDocument(
                                asList(
                                        new BsonElement("_id", _TRUE),
                                        new BsonElement("owner", _TRUE),
                                        new BsonElement("users",
                                                new BsonArray(
                                                        asList(
                                                                new BsonString("$userMaster"),
                                                                new BsonString("$members")
                                                        )
                                                )
                                        ),
                                        new BsonElement("roles", _TRUE)
                                )
                        )
                ),
                new BsonDocument("$unwind", new BsonString("$users")),
                //agrupando registros
                new BsonDocument("$group",
                        new BsonDocument(
                                asList(
                                        new BsonElement("_id",
                                                new BsonDocument(
                                                        asList(
                                                                new BsonElement("_id", new BsonString("$_id")),
                                                                new BsonElement("owner", new BsonString("$owner")),
                                                                new BsonElement("user", new BsonString("$users"))
                                                        )
                                                )
                                        ),
                                        new BsonElement("roles", new BsonDocument("$addToSet", new BsonString("$roles")))
                                )
                        )
                ),
                new BsonDocument("$project",
                        new BsonDocument(
                                asList(
                                        new BsonElement("_id",
                                                new BsonDocument("$arrayElemAt",
                                                        new BsonArray(
                                                                asList(
                                                                        new BsonDocument("$objectToArray", new BsonString("$_id.user")),
                                                                        new BsonInt32(1)
                                                                )
                                                        )
                                                )
                                        ),
                                        new BsonElement("owner", new BsonString("$_id.owner")),
                                        new BsonElement("user", new BsonString("$_id.user")),
                                        new BsonElement("roles", _TRUE)

                                )
                        )
                ),
                new BsonDocument("$project",
                        new BsonDocument(
                                asList(
                                        new BsonElement("_id", new BsonString("$_id.v")),
                                        new BsonElement("userId", new BsonString("$_id.v")),
                                        new BsonElement("owner", _TRUE),
                                        new BsonElement("user", _TRUE),
                                        new BsonElement("roles", _TRUE)
                                )
                        )
                )
        );
    }

    @Override
    public String getDescription() {
        return this.DESCRIPTION;
    }
}
