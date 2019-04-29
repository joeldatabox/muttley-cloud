package br.com.muttley.security.server.config.view.source;

import br.com.muttley.mongo.service.config.source.ViewSource;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonString;
import org.bson.conversions.Bson;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Joel Rodrigues Moreira on 26/04/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ViewMuttleyWorkTeam implements ViewSource {
    private final String VERSION = "2.0.10";
    private final String NAME = "view_muttley_work_teams";
    private final String SOURCE = "muttley-work-teams";
    private final String DESCRIPTION = "A view foi criada para facilitar a listagem de usuário por owners";

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
         db.getCollection("muttley-work-teams").aggregate([
         //pegando os dados necessários
         {$project:{ _id:1, _class:1, owner:1, userMaster:1, members:1}},
         //transformando o array
         {$unwind:'$members'},
         //transformando o array
         {$project:{ _id:1, _class:1, owner:1, records : {$objectToArray:"$$ROOT"}}},
         {$unwind:'$records'},
         //filtrando apenas os registros necessários
         {$match: {$or:[{'records.k':'members'},{'records.k':'userMaster'}]}},
         //pegando apenas os registros necessários
         {$project:{_id:1, _class:1,  owner: 1, user: '$records.v'}},
         //agrupando registros
         {$group: {
         _id: {
         _id: "$_id",
         _class: '$_class',
         owner: "$owner",
         user: "$user",
         }
         }},
         //transormando a visualização dos mesmos
         {$project:{_id:'$_id._id', _class:'$_id._class', owner:'$_id.owner', user:'$_id.user', userId: {$objectToArray:"$_id.user"}}},
         //transformando o array
         {$unwind:"$userId"},
         //filtrando apenas o necessário
         {$match:{"userId.k":"$id"}},
         //transormando a visualização dos mesmos
         {$project:{_id:1, _class:1, owner:1, user:1, userId: '$userId.v'}}
         ])
         */
        return asList(
                //pegando os dados necessários
                new BsonDocument("$project",
                        new BsonDocument(
                                asList(
                                        new BsonElement("_id", _TRUE),
                                        new BsonElement("_class", _TRUE),
                                        new BsonElement("owner", _TRUE),
                                        new BsonElement("userMaster", _TRUE),
                                        new BsonElement("members", _TRUE)
                                )
                        )
                ),
                //transformando o array
                new BsonDocument("$unwind", new BsonString("$members")),
                //transformando o array
                new BsonDocument("$project",
                        new BsonDocument(
                                asList(
                                        new BsonElement("_id", _TRUE),
                                        new BsonElement("_class", _TRUE),
                                        new BsonElement("owner", _TRUE),
                                        new BsonElement("userMaster", _TRUE),
                                        new BsonElement("members", _TRUE),
                                        new BsonElement("records",
                                                new BsonDocument("$objectToArray", new BsonString("$$ROOT"))
                                        )
                                )
                        )
                ),
                new BsonDocument("$unwind", new BsonString("$records")),
                //filtrando apenas os registros necessários
                new BsonDocument("$match",
                        new BsonDocument("$or",
                                new BsonArray(
                                        asList(
                                                new BsonDocument("records.k", new BsonString("members")),
                                                new BsonDocument("records.k", new BsonString("userMaster"))
                                        )
                                )
                        )
                ),
                //pegando apenas os registros necessários
                new BsonDocument("$project",
                        new BsonDocument(
                                asList(
                                        new BsonElement("_id", _TRUE),
                                        new BsonElement("_class", _TRUE),
                                        new BsonElement("owner", _TRUE),
                                        new BsonElement("user", new BsonString("$records.v"))
                                )
                        )
                ),
                //agrupando registros
                new BsonDocument("$group",
                        new BsonDocument("_id",
                                new BsonDocument(
                                        asList(
                                                new BsonElement("_id", new BsonString("$_id")),
                                                new BsonElement("_class", new BsonString("$_class")),
                                                new BsonElement("owner", new BsonString("$owner")),
                                                new BsonElement("user", new BsonString("$user"))
                                        )
                                )
                        )
                ),
                //transormando a visualização dos mesmos
                new BsonDocument("$project",
                        new BsonDocument(
                                asList(
                                        new BsonElement("_id", new BsonString("$_id._id")),
                                        new BsonElement("_class", new BsonString("$_id._class")),
                                        new BsonElement("owner", new BsonString("$_id.owner")),
                                        new BsonElement("user", new BsonString("$_id.user")),
                                        new BsonElement("userId", new BsonDocument("$objectToArray", new BsonString("$_id.user")))

                                )
                        )
                ),
                //transformando o array
                new BsonDocument("$unwind", new BsonString("$userId")),
                //filtrando apenas o necessário
                new BsonDocument("$match", new BsonDocument("userId.k", new BsonString("$id"))),
                //transormando a visualização dos mesmos
                new BsonDocument("$project",
                        new BsonDocument(
                                asList(
                                        new BsonElement("_id", _TRUE),
                                        new BsonElement("_class", _TRUE),
                                        new BsonElement("owner", _TRUE),
                                        new BsonElement("userId", new BsonString("$userId.v")),
                                        new BsonElement("user", _TRUE)
                                )
                        )
                )
        );
    }

    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
