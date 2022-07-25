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
import java.util.stream.Collectors;

import static br.com.muttley.mongo.service.infra.util.ListReduceBuilder.reduce;
import static br.com.muttley.mongo.service.infra.util.MapBuilder.map;
import static br.com.muttley.mongo.service.infra.util.SetUnionBuilder.setUnion;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.bind;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.graphLookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter.filter;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.Size.lengthOfArray;
import static org.springframework.data.mongodb.core.aggregation.ComparisonOperators.Eq.valueOf;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Cond.when;
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
        this.removeUsersMasterFromMembers(user, workTeam);

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
        super.checkPrecondictionSave(user, workTeam);
    }

    @Override
    public void beforeUpdate(User user, WorkTeam workTeam) {
        //garantindo que não será alterado informações cruciais
        workTeam.setOwner(user.getCurrentOwner());
        this.removeUsersMasterFromMembers(user, workTeam);
        super.beforeUpdate(user, workTeam);
    }

    @Override
    public void checkPrecondictionUpdate(User user, WorkTeam workTeam) {
        this.checkOwnerIsPresent(user, workTeam);
        this.checkUsersHasBeenPresent(user, workTeam);
        this.checkCircularDependence(user, workTeam);
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
        operations.add(0, match(where("owner.$id").is(user.getCurrentOwner().getObjectId()).and("usersMaster.$id").is(user.getObjectId())));

        final AggregationResults<WorkTeamDomain> results = this.mongoTemplate.aggregate(
                newAggregation(operations),
                documentNameConfig.getNameViewCollectionWorkTeam(),
                WorkTeamDomain.class
        );
        final WorkTeamDomain domain = results.getUniqueMappedResult();
        if (domain != null) {
            return domain
                    //adicionando membro do owner
                    .addMember(user.getCurrentOwner().getUserMaster(), true);
        }
        //se chegou até aqui é sinal que o usuário não está presente em um time
        //logo devemos retornar apenas ele e o usuário do owner para acesso aos dados
        return new WorkTeamDomain()
                //adicionando usuário atual como membro
                .addMember(user, true)
                //adicionando membro do owner
                .addMember(user.getCurrentOwner().getUserMaster(), true);
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
         * var $owner = ObjectId("61a97c9cb19886c43a02cac8");
         * var $userMaster = ObjectId("61aa06375dab550007905ed5");
         * db.getCollection("view-muttley-work-teams").aggregate([
         *     {$match:{"owner.$id":$owner,"usersMaster.$id":$userMaster}},
         *     //fazendo as consulta recursivamente para montar as dependencias
         *     {$graphLookup:{
         *         from:"muttley-work-teams",
         *         startWith:"$members",
         *         connectFromField: "members",
         *         connectToField:"usersMaster",
         *         as: "treeTeams",
         *         restrictSearchWithMatch:{"owner.$id": $owner}
         *     }},
         *     //pengando todos os subordinados encontrado e agrupando
         *     {$project:{usersMaster:1, members:1, editDataFromMembers:1, membersTree:{$reduce:{
         *         input: "$treeTeams",
         *         initialValue:[],
         *         in:{$setUnion:["$$value", "$$this.members", ["$$this.usersMaster"]]}
         *     }}}},
         *     //propagando as permissoes de edicao entre os membros
         *     {$project:{
         *         usersMaster:1,
         *         members:{$map:{
         *             input: "$members",
         *             as: "member",
         *             in: {user:"$$member", canEdit:"$editDataFromMembers"}
         *         }},
         *         membersTree:{$map:{
         *             input: "$membersTree",
         *             as: "member",
         *             in: {user:"$$member", canEdit:"$editDataFromMembers"}
         *         }},
         *     }},
         *     //agrupando subordinados encontrados juntamente com os membros atuais
         *     {$project:{usersMaster:1, members:{$setUnion:["$membersTree", "$members"]}}},
         *     //agrupando com demais work-teams que tenha sido encontrados
         *     {$group:{_id:"$usersMaster", members:{$addToSet:"$members"}}},
         *     //fazendo o processo de reduce para resultar em um array simple de usuarios
         *     {$project:{usersMaster:"$_id", members:{$reduce:{
         *         input: "$members",
         *         initialValue:[],
         *         in:{$setUnion:["$$value", "$$this"]}
         *     }}}},
         *     //distrinchando os membros agrupados para garantir que usuários com não tenha mais de uma autorização
         *     {$unwind:"$members"},
         *     //agrupando por autorização
         *     {$group:{ _id:{user:"$members.user", usersMaster:"$usersMaster"}, canEdit:{$addToSet:"$members.canEdit"}}},
         *     //vamos filtrar as autorizações agrupadas e pegar apenas as que estão com true
         *     {$project:{usersMaster:"$_id.usersMaster", member:"$_id.user", canEdit: {$filter:{
         *         input:"$canEdit",
         *         as: "item",
         *         cond:"$$item"
         *     }}}},
         *     //garantido que os itens que não foram preenchidos receba o devido status de false
         *     {$project:{usersMaster:"$_id.usersMaster", member:"$_id.user", canEdit: {$cond:[{$eq:[{$size:"$canEdit"}, 0]},false, true]}}},
         *     //agrupando por user master
         *     {$group:{ _id:"$usersMaster", members:{$addToSet:{user:"$member", canEdit:"$canEdit"}}}},
         *     //ajustando os dados
         *     {$project:{usersMaster:"$_id", members:1}}
         * ])
         */
        return new LinkedList<>(asList(
                //fazendo as consulta recursivamente para montar as dependencias
                graphLookup(documentNameConfig.getNameViewCollectionWorkTeam())
                        .startWith("$members")
                        .connectFrom("members")
                        .connectTo("usersMaster")
                        .restrict(where("owner.$id").is(user.getCurrentOwner().getObjectId()))
                        .as("treeTeams"),
                //pengando todos os subordinados encontrado e agrupando
                project("usersMaster", "members", "editDataFromMembers").and(
                        reduce(
                                "$treeTeams",
                                asList(),
                                setUnion("$$value", "$$this.members", asList("$$this.usersMaster"))
                        )
                ).as("membersTree"),
                //propagando as permissoes de edicao entre os membros
                project("usersMaster").and(
                        map(
                                "$members",
                                "member",
                                context -> new BasicDBObject("user", "$$member").append("canEdit", "$editDataFromMembers")
                        )
                ).as("members").and(
                        map(
                                "$membersTree",
                                "member",
                                context -> new BasicDBObject("user", "$$member").append("canEdit", "$editDataFromMembers")
                        )
                ).as("membersTree"),
                //agrupando subordinados encontrados juntamente com os membros atuais
                project("usersMaster").and(setUnion("$membersTree", "$members")).as("members"),
                //agrupando com demais work-teams que tenha sido encontrados
                group("$usersMaster").addToSet("members").as("members"),
                //fazendo o processo de reduce para resultar em um array simple de usuarios
                project().and("$_id").as("usersMaster").and(reduce("$members", asList(), setUnion("$$value", "$$this"))).as("members"),
                //distrinchando os membros agrupados para garantir que usuários com não tenha mais de uma autorização
                unwind("$members"),
                //agrupando por autorização
                group(bind("user", "$members.user").and("usersMaster", "$usersMaster")).addToSet("members.canEdit").as("canEdit"),
                //vamos filtrar as autorizações agrupadas e pegar apenas as que estão com true
                project().and("$_id.usersMaster").as("usersMaster").and("$_id.user").as("member").and(filter("$canEdit").as("item").by("$$item")).as("canEdit"),
                //garantido que os itens que não foram preenchidos receba o devido status de false
                project("usersMaster", "member").and(
                        when(
                                valueOf(lengthOfArray("$canEdit")).equalToValue(0)
                        ).then(false).otherwise(true)
                ).as("canEdit"),
                //agrupando por user master
                group("usersMaster").addToSet(new BasicDBObject("user", "$member").append("canEdit", "$canEdit")).as("members"),
                //ajustando os dados para retornar no padrão da collection e nao da view
                project("members").and("_id").as("userMaster")
        ));
    }

    /**
     * Precisamos garantir que o usuários master não estará jundo listado aos seus membros
     */
    private void removeUsersMasterFromMembers(final User user, final WorkTeam workTeam) {

        workTeam.setMembers(
                workTeam.getMembers()
                        .parallelStream()
                        .filter(it -> workTeam.getUsersMaster().parallelStream().filter(iit -> it.equals(iit)).count() == 0)
                        .collect(toSet())
        );
    }

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
                        match(where("members.$id").is(workTeam.getUsersMaster().parallelStream().map(User::getObjectId).collect(toSet()))),
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

    /**
     * Expirando itens presente no cache do serviço
     */
    private void expire(final WorkTeam workTeam) {
        workTeam.getUsersMaster().forEach(it -> {
            //deletando item do cache
            this.redisService.delete(LocalWorkTeamService.getBasicKey(it));
        });
    }
}
