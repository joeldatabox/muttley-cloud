package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.localcache.services.LocalWorkTeamService;
import br.com.muttley.model.BasicAggregateResultCount;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.workteam.WorkTeam;
import br.com.muttley.model.workteam.WorkTeamDomain;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import br.com.muttley.security.server.repository.WorkTeamRepository;
import br.com.muttley.security.server.service.OwnerService;
import br.com.muttley.security.server.service.UserBaseService;
import br.com.muttley.security.server.service.WorkTeamService;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
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
    private final OwnerService ownerService;
    private final DocumentNameConfig documentNameConfig;
    private final UserBaseService userBaseService;
    private final RedisService redisService;

    @Autowired
    public WorkTeamServiceImpl(final WorkTeamRepository repository, final MongoTemplate mongoTemplate, OwnerService ownerService, final DocumentNameConfig documentNameConfig, UserBaseService userBaseService, RedisService redisService) {
        super(repository, mongoTemplate, WorkTeam.class);
        this.repository = repository;
        this.ownerService = ownerService;
        this.documentNameConfig = documentNameConfig;
        this.userBaseService = userBaseService;
        this.redisService = redisService;
    }

    @Override
    public void beforeSave(User user, WorkTeam workTeam) {
        //garantindo informações cruciais
        workTeam.setOwner(user);
        //this.removeUsersMasterFromMembers(user, workTeam);

        super.beforeSave(user, workTeam);
    }

    @Override
    public void afterSave(User user, WorkTeam value) {
        this.expire(value);
        super.afterSave(user, value);
    }

    @Override
    public void afterSave(User user, Collection<WorkTeam> values) {
        values.forEach(this::expire);
        super.afterSave(user, values);
    }

    @Override
    public void checkPrecondictionSave(User user, WorkTeam workTeam) {
        this.checkOwnerIsPresent(user, workTeam);
        this.checkUsersHasBeenPresent(user, workTeam);
        this.checkCircularDependence(user, workTeam);
        this.checkUsersMasterHasBeenInMembers(user, workTeam);
        super.checkPrecondictionSave(user, workTeam);
    }

    @Override
    public void beforeUpdate(User user, WorkTeam workTeam) {
        //garantindo que não será alterado informações cruciais
        workTeam.setOwner(user.getCurrentOwner());
        //this.removeUsersMasterFromMembers(user, workTeam);
        super.beforeUpdate(user, workTeam);
    }

    @Override
    public void checkPrecondictionUpdate(User user, WorkTeam workTeam) {
        this.checkOwnerIsPresent(user, workTeam);
        this.checkUsersHasBeenPresent(user, workTeam);
        this.checkCircularDependence(user, workTeam);
        this.checkUsersMasterHasBeenInMembers(user, workTeam);
        super.checkPrecondictionUpdate(user, workTeam);
    }

    @Override
    public void afterUpdate(User user, WorkTeam value) {
        this.expire(value);
        super.afterUpdate(user, value);
    }

    @Override
    public void afterUpdate(User user, Collection<WorkTeam> values) {
        values.forEach(it -> this.expire(it));
        super.afterUpdate(user, values);
    }

    @Override
    public WorkTeamDomain loadDomain(final User user) {
        final List<AggregationOperation> operations = this.createBasicQueryViewWorkTeamDomain(user);
        //adicionando o critério de filtro inicial
        operations.add(match(where("_id").is(user.getObjectId())));

        final AggregationResults<WorkTeamDomain> results = this.mongoTemplate.aggregate(
                newAggregation(operations),
                documentNameConfig.getNameCollectionUser(),
                WorkTeamDomain.class
        );
        final WorkTeamDomain domain = results.getUniqueMappedResult();
        if (domain != null) {
            return domain
                    .setUserMaster(user)
                    //adicionando membro do owner
                    .addSupervisors(user.getCurrentOwner().getUserMaster());
        }
        //se chegou até aqui é sinal que o usuário não está presente em um time
        //logo devemos retornar apenas ele e o usuário do owner para acesso aos dados
        return new WorkTeamDomain()
                .setUserMaster(user)
                //adicionando membro do owner
                .addSupervisors(user.getCurrentOwner().getUserMaster());
    }

    @Override
    public List<WorkTeam> findAll(User user, Map<String, String> allRequestParams) {
        /**
         *db.getCollection("muttley-work-teams").aggregate([
         *    {$match:{$or:[{"userMaster.$id": ObjectId("5d49cca5a1d16f19595be983")}, {"members.$id":ObjectId("5d49cca5a1d16f19595be983")}]}},
         * ])
         */
        final AggregationResults<WorkTeam> workTeamResults = this.mongoTemplate.aggregate(
                newAggregation(
                        match(
                                new Criteria().orOperator(
                                        where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                )
                        )
                )
                , WorkTeam.class, WorkTeam.class);
        if (workTeamResults == null) {
            throw new MuttleyNoContentException(WorkTeam.class, null, "Nenhum grupo encontrado para o usuário informado");
        }
        final List<WorkTeam> workTeams = workTeamResults.getMappedResults();
        if (CollectionUtils.isEmpty(workTeams)) {
            throw new MuttleyNoContentException(WorkTeam.class, null, "Nenhum grupo encontrado para o usuário informado");
        }
        return workTeams;
    }

    @Override
    public List<WorkTeam> findByUser(User user) {
        /**
         *db.getCollection("muttley-work-teams").aggregate([
         *    {$match:{$or:[{"userMaster.$id": ObjectId("5d49cca5a1d16f19595be983")}, {"members.$id":ObjectId("5d49cca5a1d16f19595be983")}]}},
         * ])
         */
        final AggregationResults<WorkTeam> workTeamResults = this.mongoTemplate.aggregate(
                newAggregation(
                        match(
                                new Criteria().orOperator(
                                        where("userMaster.$id").is(new ObjectId(user.getId())),
                                        where("members.$id").is(new ObjectId(user.getId()))
                                )
                        )
                )
                , WorkTeam.class, WorkTeam.class);
        if (workTeamResults == null) {
            throw new MuttleyNotFoundException(WorkTeam.class, "members", "Nenhum grupo encontrado para o usuário informado");
        }
        final List<WorkTeam> workTeams = workTeamResults.getMappedResults();
        if (CollectionUtils.isEmpty(workTeams)) {
            throw new MuttleyNotFoundException(WorkTeam.class, "members", "Nenhum grupo encontrado para o usuário informado");
        }
        return workTeams;
    }

    private List<AggregationOperation> createBasicQueryViewWorkTeamDomain(final User user) {
        /**
         * var $owner = ObjectId("629f37d4e684d90007552522");
         * //var $userMaster = ObjectId("648c7726277657c615334a06");
         * var $userMaster = ObjectId("648c7726277657c615334a06");
         *
         * db.getCollection("muttley-users").aggregate([
         *     {$match:{_id:$userMaster}},
         *     //carregando times de trabalho onde o mesmo faz parte de mebros
         *     //isso se faz necessário para que carregamos os supervisores e os colegas
         *     {$lookup:{
         *         from: "muttley-work-teams",
         *         pipeline:[
         *             {$match:{
         *                 $expr: {
         *                     $and:[
         *                         {$eq:["$owner.$id", $owner]},
         *                         {$or: [
         *                             { $in: [$userMaster, "$members.$id"] }
         *                         ]}
         *                     ],
         *                 }
         *             }},
         *             //preparando dados para retorno
         *             {$project:{
         *                 usersMaster:1,
         *                 members:{
         *                     //vamos remover o usuário buscado como membro
         *                     $filter:{
         *                         input: "$members",
         *                         as:"item",
         *                         cond:{$ne:["$$item.$id", $userMaster]}
         *                     }
         *                 }
         *             }},
         *             //agrupando registros
         *             {$group:{_id:null, usersMaster:{$addToSet:"$usersMaster"}, members:{$addToSet:"$members"}}},
         *             {$project:{
         *                 supervisors:{
         *                     $reduce:{
         *                         input:"$usersMaster",
         *                         initialValue: [],
         *                         in:{$setUnion:["$$value", "$$this"]}
         *                     }
         *                 },
         *                 colleagues:{
         *                     $reduce:{
         *                         input:"$members",
         *                         initialValue: [],
         *                         in:{$setUnion:["$$value", "$$this"]}
         *                     }
         *                 }
         *             }}
         *         ],
         *         as:"supervisorsColleagues"
         *     }},
         *     //carregando times de trabalho onde o mesmo faz parte de supervisores
         *     //isso se faz necessário para que carregamos os colegas e os subordinados
         *     //Os usuraios contido em usersMaster serão colegas e os usuários contidos em members serão subordinados
         *     {$lookup:{
         *         from: "muttley-work-teams",
         *         pipeline:[
         *             {$match:{
         *                 $expr: {
         *                     $and:[
         *                         {$eq:["$owner.$id", $owner]},
         *                         {$or: [
         *                             { $in: [$userMaster, "$usersMaster.$id"] }
         *                         ]}
         *                     ],
         *                 }
         *             }},
         *             //preparando dados para retorno
         *             {$project:{
         *                 usersMaster:{
         *                     //vamos remover o usuário buscado como userMaster
         *                     $filter:{
         *                         input: "$usersMaster",
         *                         as:"item",
         *                         cond:{$ne:["$$item.$id", $userMaster]}
         *                     }
         *                 },
         *                 members:1
         *             }},
         *             //agrupando registros
         *             {$group:{_id:null, usersMaster:{$addToSet:"$usersMaster"}, members:{$addToSet:"$members"}}},
         *             {$project:{
         *                 usersMaster:{
         *                     $reduce:{
         *                         input:"$usersMaster",
         *                         initialValue: [],
         *                         in:{$setUnion:["$$value", "$$this"]}
         *                     }
         *                 },
         *                 members:{
         *                     $reduce:{
         *                         input:"$members",
         *                         initialValue: [],
         *                         in:{$setUnion:["$$value", "$$this"]}
         *                     }
         *                 }
         *             }},
         *             //fazendo as consulta recursivamente para montar as dependencias
         *             {$graphLookup:{
         *                 from:"muttley-work-teams",
         *                 startWith:"$members",
         *                 connectFromField: "members",
         *                 connectToField:"usersMaster",
         *                 as: "treeTeams",
         *                 restrictSearchWithMatch:{
         *                     //garantindo filtro pelo owner corrente
         *                     "owner.$id": $owner,
         *                     //garantindo que pegaria apenas registros onde o usuario seja membro e nao um administrador
         *                     "usersMaster.$id":{$nin:[$userMaster]}
         *                 }
         *             }},
         *             //pengando todos os subordinados encontrado e agrupando
         *             {$project:{
         *                 usersMaster:1,
         *                 members:1,
         *                 //executando reduce no campo treeTeams para concatenarmos os membros e usersMaster
         *                 //idependente de qual o resultado podemos concatenalos, pois, todos estaram no mesmo nivel da cascata
         *                 membersTree:{
         *                     $reduce:{
         *                         input: "$treeTeams",
         *                         initialValue:[],
         *                         //concatenando tudo
         *                         in:{$setUnion:["$$value", "$$this.members", "$$this.usersMaster"]}
         *                     }
         *                 }
         *             }},
         *             //ajustando dados para supervisores, colegas e subordinados
         *             {$project:{
         *                 colleagues:"$usersMaster",
         *                 subordinates:{$setUnion:["$members", "$membersTree"]}
         *             }}
         *         ],
         *         as:"colleaguesSubordinates"
         *     }},
         *     //ajustando dados para retorno
         *     {$project:{
         *         supervisors: {
         *             $ifNull:[
         *                 {$map:{
         *                     input:{$arrayElemAt:["$supervisorsColleagues.supervisors", 0]},
         *                     as:"item",
         *                     //por padrao so sera acessado dados publicos dos supervisores
         *                     in:{user:"$$item", domain:"PUBLIC"}
         *                 }}
         *             ,
         *             []
         *            ]
         *         },
         *         colleagues:{
         *             $ifNull:[
         *                 {$map:{
         *                     input:{
         *                         $setUnion:[
         *                             {$arrayElemAt:["$supervisorsColleagues.colleagues", 0]},
         *                             {$arrayElemAt:["$colleaguesSubordinates.collegues", 0]}
         *                         ]
         *                     },
         *                     as:"item",
         *                     //por padrao so sera acessado dados publicos ou restritos dos colegas
         *                     in:{user:"$$item", domain:"RESTRICTED"}
         *                 }}
         *             ,
         *             []]
         *         },
         *         subordinates: {
         *             $ifNull:[
         *                 {$map:{
         *                     input:{$arrayElemAt:["$colleaguesSubordinates.subordinates", 0]},
         *                     as:"item",
         *                     //por padrao podemos acessar qualquer registros de subordinados
         *                     in:{user:"$$item", domain:null}
         *                 }}
         *                 ,
         *                 []
         *             ]
         *         }
         *     }}
         * ])
         */
        final List<AggregationOperation> operations = new LinkedList<>();
        //operations.add(match(where("_id").is(user.getObjectId())));
        //carregando times de trabalho onde o mesmo faz parte de mebros
        //isso se faz necessário para que carregamos os supervisores e os colegas
        operations.add(
                //carregando times de trabalho onde o mesmo faz parte de mebros
                //isso se faz necessário para que carregamos os supervisores e os colegas
                context -> new BasicDBObject("$lookup",
                        new BasicDBObject("from", this.documentNameConfig.getNameCollectionWorkTeam())
                                .append("pipeline", asList(
                                        new BasicDBObject("$match",
                                                new BasicDBObject("$expr",
                                                        new BasicDBObject("$and", asList(
                                                                new BasicDBObject("$eq", asList("$owner.$id", user.getCurrentOwner().getObjectId())),
                                                                new BasicDBObject("$or", asList(new BasicDBObject("$in", asList(user.getObjectId(), "$members.$id"))))
                                                        ))
                                                )
                                        ),
                                        //preparando dados para retorno
                                        new BasicDBObject("$project",
                                                new BasicDBObject("usersMaster", 1)
                                                        .append("members",
                                                                new BasicDBObject("$filter",
                                                                        new BasicDBObject("input", "$members")
                                                                                .append("as", "item")
                                                                                .append("cond",
                                                                                        new BasicDBObject("$ne", asList("$$item.$id", user.getObjectId()))
                                                                                )
                                                                )
                                                        ).append("aux", "_null")
                                        ),
                                        //agrupando registros
                                        new BasicDBObject("$group",
                                                new BasicDBObject("_id", "$aux")
                                                        .append("usersMaster", new BasicDBObject("$addToSet", "$usersMaster"))
                                                        .append("members", new BasicDBObject("$addToSet", "$members"))
                                        ),
                                        new BasicDBObject("$project",
                                                new BasicDBObject("supervisors",
                                                        new BasicDBObject("$reduce",
                                                                new BasicDBObject("input", "$usersMaster")
                                                                        .append("initialValue", asList())
                                                                        .append("in",
                                                                                new BasicDBObject("$setUnion", asList("$$value", "$$this"))
                                                                        )
                                                        )
                                                ).append("colleagues",
                                                        new BasicDBObject("$reduce",
                                                                new BasicDBObject("input", "$members")
                                                                        .append("initialValue", asList())
                                                                        .append("in",
                                                                                new BasicDBObject("$setUnion", asList("$$value", "$$this"))
                                                                        )
                                                        )
                                                )
                                        )
                                )).append("as", "supervisorsColleagues")
                )
        );
        operations.add(
                //carregando times de trabalho onde o mesmo faz parte de supervisores
                //isso se faz necessário para que carregamos os colegas e os subordinados
                //Os usuraios contido em usersMaster serão colegas e os usuários contidos em members serão subordinados
                context -> new BasicDBObject("$lookup",
                        new BasicDBObject("from", this.documentNameConfig.getNameCollectionWorkTeam())
                                .append("pipeline", asList(
                                        new BasicDBObject("$match",
                                                new BasicDBObject("$expr",
                                                        new BasicDBObject("$and", asList(
                                                                new BasicDBObject("$eq", asList("$owner.$id", user.getCurrentOwner().getObjectId())),
                                                                new BasicDBObject("$or", asList(new BasicDBObject("$in", asList(user.getObjectId(), "$usersMaster.$id"))))
                                                        ))
                                                )
                                        ),
                                        //preparando dados para retorno
                                        new BasicDBObject("$project",
                                                new BasicDBObject("usersMaster",
                                                        //vamos remover o usuário buscado como userMaster
                                                        new BasicDBObject("$filter",
                                                                new BasicDBObject("input", "$usersMaster")
                                                                        .append("as", "item")
                                                                        .append("cond",
                                                                                new BasicDBObject("$ne", asList("$$item.$id", user.getObjectId()))
                                                                        )
                                                        )
                                                ).append("members", 1)
                                        ),
                                        //agrupando registros
                                        new BasicDBObject("$group",
                                                new BasicDBObject("_id", null)
                                                        .append("usersMaster", new BasicDBObject("$addToSet", "$usersMaster"))
                                                        .append("members", new BasicDBObject("$addToSet", "$members"))
                                        ),

                                        new BasicDBObject("$project",
                                                new BasicDBObject("usersMaster",
                                                        new BasicDBObject("$reduce",
                                                                new BasicDBObject("input", "$usersMaster")
                                                                        .append("initialValue", asList())
                                                                        .append("in", new BasicDBObject("$setUnion", asList("$$value", "$$this")))
                                                        )
                                                ).append("members",
                                                        new BasicDBObject("$reduce",
                                                                new BasicDBObject("input", "$members")
                                                                        .append("initialValue", asList())
                                                                        .append("in", new BasicDBObject("$setUnion", asList("$$value", "$$this")))
                                                        )
                                                )
                                        ),
                                        new BasicDBObject("$graphLookup",
                                                new BasicDBObject("from", this.documentNameConfig.getNameCollectionWorkTeam())
                                                        .append("startWith", "$members")
                                                        .append("connectFromField", "members")
                                                        .append("connectToField", "usersMaster")
                                                        .append("restrictSearchWithMatch",
                                                                //garantindo filtro pelo owner corrente
                                                                new BasicDBObject("owner.$id", user.getCurrentOwner().getObjectId())
                                                                        //garantindo que pegaria apenas registros onde o usuario seja membro e nao um administrador
                                                                        .append("usersMaster.$id", new BasicDBObject("$nin", asList(user.getObjectId())))
                                                        ).append("as", "treeTeams")
                                        ),
                                        //pengando todos os subordinados encontrado e agrupando
                                        new BasicDBObject("$project",
                                                new BasicDBObject("usersMaster", 1)
                                                        .append("members", 1)
                                                        //executando reduce no campo treeTeams para concatenarmos os membros e usersMaster
                                                        //idependente de qual o resultado podemos concatenalos, pois, todos estaram no mesmo nivel da cascata
                                                        .append("membersTree",
                                                                new BasicDBObject("$reduce",
                                                                        new BasicDBObject("input", "$treeTeams")
                                                                                .append("initialValue", asList())
                                                                                //concatenando tudo
                                                                                .append("in",
                                                                                        new BasicDBObject("$setUnion", asList("$$value", "$$this.members", "$$this.usersMaster"))
                                                                                )
                                                                )
                                                        )
                                        ),
                                        //ajustando dados para supervisores, colegas e subordinados
                                        new BasicDBObject("$project",
                                                new BasicDBObject("colleagues", "$usersMaster")
                                                        .append("subordinates", new BasicDBObject("$setUnion", asList("$members", "$membersTree")))
                                        )
                                )).append("as", "colleaguesSubordinates")
                )
        );
        operations.add(
                //ajustando dados para retorno
                project()
                        .and(context1 ->
                                new BasicDBObject("$ifNull", asList(
                                        new BasicDBObject("$map",
                                                new BasicDBObject("input",
                                                        new BasicDBObject("$arrayElemAt", asList("$supervisorsColleagues.supervisors", 0))
                                                ).append("as", "item")
                                                        //por padrao so sera acessado dados publicos dos supervisores
                                                        .append("in",
                                                                new BasicDBObject("user", "$$item").append("domain", "PUBLIC")
                                                        )
                                        ),
                                        asList()
                                ))
                        ).as("supervisors")
                        .and(context1 ->
                                new BasicDBObject("$ifNull", asList(
                                        new BasicDBObject("$map",
                                                new BasicDBObject("input",
                                                        new BasicDBObject("$setUnion", asList(
                                                                new BasicDBObject("$arrayElemAt", asList("$supervisorsColleagues.colleagues", 0)),
                                                                new BasicDBObject("$arrayElemAt", asList("$colleaguesSubordinates.collegues", 0))
                                                        ))
                                                ).append("as", "item")
                                                        //por padrao so sera acessado dados publicos ou restritos dos colegas
                                                        .append("in",
                                                                new BasicDBObject("user", "$$item").append("domain", "RESTRICTED")
                                                        )
                                        ),
                                        asList()
                                ))
                        ).as("colleagues")
                        .and(context1 ->
                                new BasicDBObject("$ifNull", asList(
                                        new BasicDBObject("$map",
                                                new BasicDBObject("input",
                                                        new BasicDBObject("$arrayElemAt", asList("$colleaguesSubordinates.subordinates", 0))
                                                ).append("as", "item")
                                                        //por padrao podemos acessar qualquer registros de subordinados
                                                        .append("in",
                                                                new BasicDBObject("user", "$$item").append("domain", null)
                                                        )
                                        ),
                                        asList()
                                ))
                        ).as("subordinates")
        );
        return operations;
    }

    /**
     * Precisamos garantir que o usuários master não estará jundo listado aos seus membros
     */
    /*private void removeUsersMasterFromMembers(final User user, final WorkTeam workTeam) {

        workTeam.setMembers(
                workTeam.getMembers()
                        .parallelStream()
                        .filter(it -> workTeam.getUsersMaster().parallelStream().filter(iit -> it.equals(iit)).count() == 0)
                        .collect(toSet())
        );
    }*/
    private void checkOwnerIsPresent(final User user, final WorkTeam workTeam) {
        final Owner owner = user.getCurrentOwner();
        final User userMaster;
        if (owner.getUserMaster() != null) {
            userMaster = owner.getUserMaster();
        } else {
            userMaster = ownerService.loadCurrentOwner(user).getUserMaster();
        }

        if (workTeam.containsMember(userMaster)) {
            throw new MuttleyBadRequestException(WorkTeam.class, "members", "O owner do sistema não pode estar entre os membros do time de trabalho");
        }
    }

    /**
     * Verifica se todos os usuários inclusos fazem parte da mesma base de usuário que o user master
     */
    private void checkUsersHasBeenPresent(final User user, final WorkTeam workTeam) {
        //verficando se tem algum usuário presente no workteam
        if (workTeam.containsAnyUser()) {
            //para evitar consultas desmasiadas, vamos pegar todos os usuario e verificar se estão presentes
            //na base de usuários
            if (!this.userBaseService.allHasBeenIncludedGroup(user, workTeam.getAllUsers())) {
                //se chegou até aqui é sinal que existe algum usuário que não está presente na base
                //logo precisamos checar um a um para garantir

                final Map<String, Object> details = new HashMap<>();

                //verificando o user master
                if (!workTeam.usersMasterIsEmpty()) {
                    workTeam.getUsersMaster()
                            .stream()
                            .filter(it -> !this.userBaseService.hasBeenIncludedAnyGroup(user, it))
                            .forEach(it -> {
                                details.put("usersMaster." + it.getUserName(), "O usuário " + it.getName() + " não está presente na base de dados");
                            });
                }

                //verificando demais membros
                workTeam.getMembers()
                        .stream()
                        .filter(it -> !this.userBaseService.hasBeenIncludedAnyGroup(user, it))
                        .forEach(it -> {
                            details.put("members." + it.getUserName(), "O usuário " + it.getName() + " não está presente na base de dados");
                        });

                throw new MuttleyBadRequestException(WorkTeam.class, null, null).addDetails(details);
            }
        }
    }

    private void checkCircularDependence(final User user, final WorkTeam workTeam) {
        final List<AggregationOperation> operations = this.createBasicQueryViewWorkTeamDomain(user);
        //para realizar a checkagem de dependencia circular, precisamos verificar se os membros estão acima do usermaster
        //para isso devemos buscar os membro como userMaster e o userMaster atual não pode ser listado como membro na consulta

        operations.add(0,
                match(
                        where("_id").in(workTeam.getMembers().parallelStream().map(User::getObjectId).collect(toSet()))
                )
        );

        operations.addAll(
                asList(
                        match(where("subordinates.user.$id").in(workTeam.getUsersMaster().parallelStream().map(User::getObjectId).collect(toSet()))),
                        Aggregation.count().as("result")
                )
        );

        final AggregationResults<BasicAggregateResultCount> globalResults = this.mongoTemplate.aggregate(
                newAggregation(operations),
                documentNameConfig.getNameCollectionUser(),
                BasicAggregateResultCount.class
        );

        //se o resultado é maior que zero logo tem uma dependencia circular e precisamos verificar qual usuário que está causando isso
        //para isso vamo consultar usuário por usuário
        final BasicAggregateResultCount globalResultCount = globalResults.getUniqueMappedResult();
        if (globalResultCount != null && globalResultCount.getResult() > 0) {


            final Set<String> usersNames = new HashSet<>();

            workTeam.getMembers().forEach(it -> {
                //removendo o filtro inicial para fazer a consulta por usuário
                operations.remove(0);

                operations.add(0,
                        match(
                                where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                        .and("usersMaster.$id").in(it.getObjectId())
                        )
                );

                final AggregationResults<BasicAggregateResultCount> results = this.mongoTemplate.aggregate(
                        newAggregation(operations),
                        WorkTeam.class,
                        BasicAggregateResultCount.class
                );
                if (results != null && results.getUniqueMappedResult() != null && results.getUniqueMappedResult().getResult() > 0) {
                    usersNames.add(it.getName());
                }

            });

            throw new MuttleyBadRequestException(WorkTeam.class, "members", "Existe membros que são superiores ao supervisor selecionado")
                    .addDetails("userNames", usersNames);
        }

    }

    private void checkUsersMasterHasBeenInMembers(final User user, final WorkTeam workTeam) {
        //verificando tem usuários masters como membros
        final Set<User> users = workTeam.getUsersMaster().parallelStream()
                .filter(it -> workTeam.getMembers().contains(it))
                .collect(toSet());
        if (!users.isEmpty()) {
            final MuttleyBadRequestException exception = new MuttleyBadRequestException(WorkTeam.class, "members", "Gestores não podem estar presentes como membros a serem geridos");
            exception.addDetails("members", users.parallelStream().map(User::getName).collect(toSet()));
            throw exception;
        }
    }


    /**
     * Expirando itens presente no cache do serviço
     */
    private void expire(final WorkTeam workTeam) {
        redisService.deleteByExpression(LocalWorkTeamService.getBasicKeyExpressionOwner(workTeam.getOwner()) + "*");
        /*workTeam.getUsersMaster().forEach(it -> {
            //deletando item do cache
            this.redisService.delete(LocalWorkTeamService.getBasicKey(it.getCurrentOwner(), it));
        });*/
    }
}
