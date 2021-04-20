package br.com.muttley.muttleyadminserver.service.impl;

import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.admin.AdminOwner;
import br.com.muttley.model.admin.AdminWorkTeam;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.muttleyadminserver.config.model.DocumentNameConfig;
import br.com.muttley.muttleyadminserver.repository.AdminWorkTeamRepository;
import br.com.muttley.muttleyadminserver.service.AdminWorkTeamService;
import org.bson.BsonDocument;
import org.bson.BsonElement;
import org.bson.BsonObjectId;
import org.bson.BsonString;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Joel Rodrigues Moreira 20/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class AdminWorkTeamServiceImpl extends AdminServiceImpl<AdminWorkTeam> implements AdminWorkTeamService {
    private final DocumentNameConfig documentNameConfig;

    @Autowired
    public AdminWorkTeamServiceImpl(AdminWorkTeamRepository repository, final MongoTemplate mongoTemplate, final DocumentNameConfig documentNameConfig) {
        super(repository, mongoTemplate, AdminWorkTeam.class);
        this.documentNameConfig = documentNameConfig;
    }

    @Override
    public AdminWorkTeam findByName(final AdminOwner owner, final String name) {
        final AdminWorkTeam owt = this.mongoTemplate.findOne(
                query(
                        where("owner.$id").is(owner.getId()).and("name").is(name)
                ), AdminWorkTeam.class
        );
        if (owt == null) {
            throw new MuttleyNotFoundException(AdminWorkTeam.class, "name", "Registro não encontrado");
        }
        return owt;
    }

    @Override
    public List<AdminWorkTeam> loadAllWorkTeams(final User user) {
        /*
        db['odin-work-teams'].aggregate(
            {
              "$match":{
                  "owner.$id": ObjectId("5a984453aa9f6634ac2e461c"),
                  "members": {"$in":[{"$ref":"users", "$id":ObjectId("5a984453aa9f6634ac2e461d")}]}
              }
            }
        )
        */
        AggregationResults<AdminWorkTeam> result = this.mongoTemplate.aggregate(
                newAggregation(
                        match(
                                where("members")
                                        .in(new BsonDocument(
                                                        asList(
                                                                new BsonElement("$ref", new BsonString("users")),
                                                                new BsonElement("$id", new BsonObjectId(new ObjectId(user.getId())))
                                                        )
                                                )
                                        )
                        )
                ),
                this.documentNameConfig.getNameCollectionAdminWorkTeam(),
                AdminWorkTeam.class);

        final List<AdminWorkTeam> list = result.getMappedResults();
        if (CollectionUtils.isEmpty(list)) {
            throw new MuttleyNoContentException(WorkTeam.class, null, "Nenhum grupo de trabalho encontrado");
        }
        return list;
    }
}
