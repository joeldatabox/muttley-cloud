package br.com.muttley.security.server.service.impl;

import br.com.muttley.model.security.User;
import br.com.muttley.model.workteam.WorkTeam;
import br.com.muttley.model.workteam.WorkTeamDomain;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import br.com.muttley.security.server.repository.WorkTeamRepository;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import static br.com.muttley.mongo.service.infra.util.ListReduceBuilder.reduce;
import static br.com.muttley.mongo.service.infra.util.SetUnionBuilder.setUnion;
import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.graphLookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 03/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class WorkTeamServiceImpl extends SecurityServiceImpl<WorkTeam> implements WorkTeamService {
    private final WorkTeamRepository repository;
    private final DocumentNameConfig documentNameConfig;

    @Autowired
    public WorkTeamServiceImpl(final WorkTeamRepository repository, final MongoTemplate mongoTemplate, final DocumentNameConfig documentNameConfig) {
        super(repository, mongoTemplate, WorkTeam.class);
        this.repository = repository;
        this.documentNameConfig = documentNameConfig;
    }

    @Override
    public void beforeSave(User user, WorkTeam workTeam) {
        //garantindo informações cruciais
        workTeam.setOwner(user);
        super.beforeSave(user, workTeam);
    }

    @Override
    public void checkPrecondictionSave(User user, WorkTeam value) {
        super.checkPrecondictionSave(user, value);
    }

    @Override
    public void beforeUpdate(User user, WorkTeam workTeam) {
        //garantindo que não será alterado informações cruciais
        workTeam.setOwner(user.getCurrentOwner());
        super.beforeUpdate(user, workTeam);
    }

    @Override
    public WorkTeamDomain loadDomain(final User user) {
        /**
         * var $owner = ObjectId("5e28b3e3637e580001e465d6");
         * db.getCollection("muttley-work-teams").aggregate([
         *
         *     {$match:{"owner.$id":$owner,"userMaster.$id":ObjectId("5e28bcf8637e580001e465e1")}},
         *     //fazendo as consulta recursivamente para montar as dependencias
         *     {$graphLookup:{
         *         from:"muttley-work-teams",
         *         startWith:"$members",
         *         connectFromField: "members",
         *         connectToField:"userMaster",
         *         as: "treeTeams",
         *         restrictSearchWithMatch:{"owner.$id": $owner}
         *     }},
         *     //pengando todos os subordinados encontrado e agrupando
         *     {$project:{userMaster:1, members:1, membersTree:{$reduce:{
         *         input: "$treeTeams",
         *         initialValue:[],
         *         in:{$setUnion:["$$value", "$$this.members", ["$$this.userMaster"]]}
         *     }}}},
         *     //agrupando subordinados encontrados juntamente com os membros atuais
         *     {$project:{userMaster:1, members:{$setUnion:["$membersTree", "$members"]}}},
         *     //agrupando com demais work-teams que tenha sido encontrados
         *     {$group:{_id:"$userMaster", members:{$addToSet:"$members"}}},
         *     //fazendo o processo de reduce para resultar em um array simple de usuarios
         *     {$project:{userMaster:"$_id", members:{$reduce:{
         *         input: "$members",
         *         initialValue:[],
         *         in:{$setUnion:["$$value", "$$this"]}
         *     }}}}
         * ])
         */
        final AggregationResults<WorkTeamDomain> results = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId()).and("userMaster.$id").is(user.getObjectId())),
                        //fazendo as consulta recursivamente para montar as dependencias
                        graphLookup(documentNameConfig.getNameCollectionWorkTeam())
                                .startWith("$members")
                                .connectFrom("members")
                                .connectTo("userMaster")
                                .restrict(where("owner.$id").is(user.getCurrentOwner().getObjectId()))
                                .as("treeTeams"),
                        //pengando todos os subordinados encontrado e agrupando
                        project("userMaster", "members").and(
                                reduce(
                                        "$treeTeams",
                                        asList(),
                                        setUnion("$$value", "$$this.members", asList("$$this.userMaster"))
                                )
                        ).as("membersTree"),
                        //agrupando subordinados encontrados juntamente com os membros atuais
                        project("userMaster").and(setUnion("$membersTree", "$members")).as("members"),
                        //agrupando com demais work-teams que tenha sido encontrados
                        group("$userMaster").addToSet("members").as("members"),
                        //fazendo o processo de reduce para resultar em um array simple de usuarios
                        project().and("$_id").as("userMaster").and(reduce("$members", asList(), setUnion("$$value", "$$this"))).as("members")
                ),
                WorkTeam.class,
                WorkTeamDomain.class
        );
        return results.getUniqueMappedResult();
    }
}
