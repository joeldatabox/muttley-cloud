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
import org.bson.BsonString;
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

import static br.com.muttley.model.security.domain.Domain.PUBLIC;
import static br.com.muttley.model.security.domain.Domain.RESTRICTED;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
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
        operations.add(0,
                match(
                        where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                .orOperator(
                                        where("usersMaster.$id").is(user.getObjectId()),
                                        where("members.$id").is(user.getObjectId())
                                )
                )
        );

        final AggregationResults<WorkTeamDomain> results = this.mongoTemplate.aggregate(
                newAggregation(operations),
                documentNameConfig.getNameViewCollectionWorkTeam(),
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
         * var $userMaster = ObjectId("63f75c0b6aba3346954ea371");
         * //var $userMaster = ObjectId("629f37c1e684d9000755251f");
         *
         * db.getCollection("muttley-work-teams").aggregate([
         *     //buscando workteams em que o usuario e membro ou gestor
         *     {$match:{
         *         "owner.$id":$owner,
         *         $or:[{"usersMaster.$id":$userMaster}, {"members.$id":$userMaster}]
         *     }},
         *
         *     //fazendo as consulta recursivamente para montar as dependencias
         *     {$graphLookup:{
         *         from:"muttley-work-teams",
         *         startWith:"$members",
         *         connectFromField: "members",
         *         connectToField:"usersMaster",
         *         as: "treeTeams",
         *         restrictSearchWithMatch:{
         *             //garantindo filtro pelo owner corrente
         *             "owner.$id": $owner,
         *             //garantindo que pegaria apenas registros onde o usuario seja membro e nao um administrador
         *             "usersMaster.$id":{$nin:[$userMaster]}
         *         }
         *     }},
         *
         *     //pengando todos os subordinados encontrado e agrupando
         *     {$project:{
         *         usersMaster:1,
         *         members:1,
         *         //criando a tipagem
         *         //definimos o tipo com base no campo treeTeams
         *         //se ela estiver vazia log e supervisores
         *         type:{$cond:[{$eq:["$treeTeams", []]}, "supervisors","subordinates"]},
         *         treeTeams:1,
         *         //executando reduce no campo treeTeams para concatenarmos os membros e usersMaster
         *         //idependente de qual o resultado podemos concatenalos, pois, todos estaram no mesmo nivel da cascata
         *         membersTree:{
         *             $reduce:{
         *                 input: "$treeTeams",
         *                 initialValue:[],
         *                 //concatenando tudo
         *                 in:{$setUnion:["$$value", "$$this.members", "$$this.usersMaster"]}
         *             }
         *         }
         *     }},
         *     //ajustando dados para supervisores, colegas e subordinados
         *     {$project:{
         *         supervisors:{
         *             $cond:[
         *                 //registros marcados com o type supervisors contem os supervisores esperados
         *                 //logo, devemos pegar os usuarios do campo usersMaster
         *                 {$eq:["$type", "supervisors"]},
         *                 //garantindo que nao sera inserido o ususario corrente
         *                 {$filter:{
         *                     input:"$usersMaster",
         *                     as: "item",
         *                     cond:{$ne:["$$item.$id", $userMaster]}
         *                 }},
         *                 []
         *             ]
         *         },
         *         colleagues:{
         *             $cond:[
         *                 //registros marcados com o type supervisors contem os colegas de trabalho esperados
         *                 //logo, devemos pegar os usuarios do campo members
         *                 {$eq:["$type", "supervisors"]},
         *                 //garantindo que nao sera inserido o ususario corrente
         *                 {$filter:{
         *                     input:"$members",
         *                     as: "item",
         *                     cond:{$ne:["$$item.$id", $userMaster]}
         *                 }},
         *                 []
         *             ]
         *         },
         *         subordinates:{
         *             $cond:[
         *                 //registros marcados com o type subordinates contem os subordinados esperados
         *                 //logo, devemos pegar os usuarios do campo membersTree
         *                 {$eq:["$type", "subordinates"]},
         *                 //garantindo que nao sera inserido o ususario corrente
         *                 {$filter:{
         *                     input:"$membersTree",
         *                     as: "item",
         *                     cond:{$ne:["$$item.$id", $userMaster]}
         *                 }},
         *                 []
         *             ]
         *         }
         *     }},
         *
         *     //agrupando usuarios encontrados
         *     {$group:{_id:"null",supervisors:{$addToSet: "$supervisors"},colleagues:{$addToSet:"$colleagues"}, subordinates:{$addToSet:"$subordinates"}}},
         *     //ajustando os dados de acordo com o modelo esperado
         *     {$project:{
         *         supervisors:{
         *             $map:{
         *                 input: {
         *                     //fazendo o processo de reduce para transform o array bidimencional
         *                     $reduce:{
         *                         input:"$supervisors",
         *                         initialValue:[],
         *                         in:{$setUnion: ["$$value", "$$this"]}
         *                     }
         *                 },
         *                 as:"item",
         *                 //por padrao so sera acessado dados publicos dos supervisores
         *                 in:{user:"$$item", domain:"PUBLIC"}
         *             }
         *         },
         *         colleagues:{
         *             $map:{
         *                 input: {
         *                     //fazendo o processo de reduce para transform o array bidimencional
         *                     $reduce:{
         *                         input:"$colleagues",
         *                         initialValue:[],
         *                         in:{$setUnion: ["$$value", "$$this"]}
         *                     }
         *                 },
         *                 as:"item",
         *                 //por padrao so sera acessado dados publicos ou restritos dos colegas
         *                 in:{user:"$$item", domain:"RESTRICTED"}
         *             }
         *         },
         *         subordinates:{
         *             $map:{
         *                 input: {
         *                     //fazendo o processo de reduce para transform o array bidimencional
         *                     $reduce:{
         *                         input:"$subordinates",
         *                         initialValue:[],
         *                         in:{$setUnion: ["$$value", "$$this"]}
         *                     }
         *                 },
         *                 as:"item",
         *                 //por padrao podemos acessar qualquer registros de subordinados
         *                 in:{user:"$$item", domain:null}
         *             }
         *         }
         *     }}
         * ])
         */
        return new LinkedList<>(asList(
                //buscando workteams em que o usuario e membro ou gestor
                /*match(
                        where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                .orOperator(
                                        where("usersMaster.$id").is(user.getObjectId()),
                                        where("members.$id").is(user.getObjectId())
                                )
                ),*/
                //fazendo as consulta recursivamente para montar as dependencias
                graphLookup(documentNameConfig.getNameCollectionWorkTeam())
                        .startWith("$members")
                        .connectFrom("members")
                        .connectTo("usersMaster")
                        .restrict(
                                //garantindo filtro pelo owner corrente
                                where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                        //garantindo que pegaria apenas registros onde o usuario seja membro e nao um administrador
                                        .and("usersMaster.$id").nin(asList(user.getObjectId()))
                        ).as("treeTeams"),
                //pengando todos os subordinados encontrado e agrupando
                project("usersMaster", "members", "treeTeams")
                        //criando a tipagem
                        //definimos o tipo com base no campo treeTeams
                        //se ela estiver vazia log e supervisores
                        .and(context ->
                                new BasicDBObject("$cond",
                                        asList(
                                                new BasicDBObject("$eq", asList("$treeTeams", asList())),
                                                "supervisors",
                                                "subordinates"
                                        )
                                )
                        ).as("type")
                        //executando reduce no campo treeTeams para concatenarmos os membros e usersMaster
                        //idependente de qual o resultado podemos concatenalos, pois, todos estaram no mesmo nivel da cascata
                        .and(context ->
                                new BasicDBObject("$reduce",
                                        new BasicDBObject("input", "$treeTeams")
                                                .append("initialValue", asList())
                                                //concatenando tudo
                                                .append("in",
                                                        new BasicDBObject("$setUnion", asList("$$value", "$$this.members", "$$this.usersMaster"))
                                                )
                                )
                        ).as("membersTree"),
                //ajustando dados para supervisores, colegas e subordinados
                project()
                        .and(context ->
                                new BasicDBObject("$cond",
                                        asList(
                                                //registros marcados com o type supervisors contem os supervisores esperados
                                                //logo, devemos pegar os usuarios do campo usersMaster
                                                new BasicDBObject("$eq", asList("$type", "supervisors")),
                                                //garantindo que nao sera inserido o ususario corrente
                                                new BasicDBObject("$filter",
                                                        new BasicDBObject("input", "$usersMaster")
                                                                .append("as", "item")
                                                                .append("cond",
                                                                        new BasicDBObject("$ne", asList("$$item.$id", user.getObjectId()))
                                                                )
                                                ),
                                                asList()
                                        )
                                )
                        ).as("supervisors")
                        .and(context ->
                                new BasicDBObject("$cond",
                                        asList(
                                                //registros marcados com o type supervisors contem os colegas de trabalho esperados
                                                //logo, devemos pegar os usuarios do campo members
                                                new BasicDBObject("$eq", asList("$type", "supervisors")),
                                                //garantindo que nao sera inserido o ususario corrente
                                                new BasicDBObject("$filter",
                                                        new BasicDBObject("input", "$members")
                                                                .append("as", "item")
                                                                .append("cond",
                                                                        new BasicDBObject("$ne", asList("$$item.$id", user.getObjectId()))
                                                                )
                                                ),
                                                asList()
                                        )
                                )
                        ).as("colleagues")
                        .and(context ->
                                new BasicDBObject("$cond",
                                        asList(
                                                //registros marcados com o type subordinates contem os subordinados esperados
                                                //logo, devemos pegar os usuarios do campo membersTree
                                                new BasicDBObject("$eq", asList("$type", "subordinates")),
                                                //garantindo que nao sera inserido o ususario corrente
                                                new BasicDBObject("$filter",
                                                        new BasicDBObject("input", "$membersTree")
                                                                .append("as", "item")
                                                                .append("cond",
                                                                        new BasicDBObject("$ne", asList("$$item.$id", user.getObjectId()))
                                                                )
                                                ),
                                                asList()
                                        )
                                )
                        ).as("subordinates")
                        .and(context -> new BasicDBObject("aux", new BsonString("1"))).as("aux"),
                //agrupando usuarios encontrados
                group("$aux")
                        .addToSet("$supervisors").as("supervisors")
                        .addToSet("$colleagues").as("colleagues")
                        .addToSet("$subordinates").as("subordinates"),
                //ajustando os dados de acordo com o modelo esperado
                project()
                        .and(context ->
                                new BasicDBObject("$map",
                                        new BasicDBObject("input",
                                                //fazendo o processo de reduce para transform o array bidimencional
                                                new BasicDBObject("$reduce",
                                                        new BasicDBObject("input", "$supervisors")
                                                                .append("initialValue", asList())
                                                                .append("in",
                                                                        new BasicDBObject("$setUnion", asList("$$value", "$$this"))
                                                                )
                                                )
                                        )
                                                .append("as", "item")
                                                //por padrao so sera acessado dados publicos dos supervisores
                                                .append("in",
                                                        new BasicDBObject("user", "$$item").append("domain", PUBLIC.name())
                                                )
                                )
                        ).as("supervisors")
                        .and(context ->
                                new BasicDBObject("$map",
                                        new BasicDBObject("input",
                                                //fazendo o processo de reduce para transform o array bidimencional
                                                new BasicDBObject("$reduce",
                                                        new BasicDBObject("input", "$colleagues")
                                                                .append("initialValue", asList())
                                                                .append("in",
                                                                        new BasicDBObject("$setUnion", asList("$$value", "$$this"))
                                                                )
                                                )
                                        )
                                                .append("as", "item")
                                                //por padrao so sera acessado dados publicos ou restritos dos colegas
                                                .append("in",
                                                        new BasicDBObject("user", "$$item").append("domain", RESTRICTED.name())
                                                )
                                )
                        ).as("colleagues")
                        .and(context ->
                                new BasicDBObject("$map",
                                        new BasicDBObject("input",
                                                //fazendo o processo de reduce para transform o array bidimencional
                                                new BasicDBObject("$reduce",
                                                        new BasicDBObject("input", "$subordinates")
                                                                .append("initialValue", asList())
                                                                .append("in",
                                                                        new BasicDBObject("$setUnion", asList("$$value", "$$this"))
                                                                )
                                                )
                                        )
                                                .append("as", "item")
                                                //por padrao podemos acessar qualquer registros de subordinados
                                                .append("in",
                                                        new BasicDBObject("user", "$$item").append("domain", null)
                                                )
                                )
                        ).as("subordinates")
        ));
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
                        where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                //filtrando os mebro como user master
                                .and("usersMaster.$id").in(workTeam.getMembers().parallelStream().map(User::getObjectId).collect(toSet()))
                )
        );

        operations.addAll(
                asList(
                        match(where("members.user.$id").in(workTeam.getUsersMaster().parallelStream().map(User::getObjectId).collect(toSet()))),
                        Aggregation.count().as("result")
                )
        );

        final AggregationResults<BasicAggregateResultCount> globalResults = this.mongoTemplate.aggregate(
                newAggregation(operations),
                documentNameConfig.getNameViewCollectionWorkTeam(),
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
                                        //filtrando os mebro como user master
                                        .and("usersMaster.$id").is(it.getObjectId())
                        )
                );

                final AggregationResults<BasicAggregateResultCount> results = this.mongoTemplate.aggregate(
                        newAggregation(operations),
                        WorkTeam.class,
                        BasicAggregateResultCount.class
                );
                if (results.getUniqueMappedResult().getResult() > 0) {
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
