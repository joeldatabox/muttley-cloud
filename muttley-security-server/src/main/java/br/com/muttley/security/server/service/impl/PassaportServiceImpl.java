package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.localcache.services.LocalRolesService;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.Passaport;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserData;
import br.com.muttley.model.security.events.ValidateOwnerInWorkGroupEvent;
import br.com.muttley.model.security.rolesconfig.AvaliableRoles;
import br.com.muttley.model.security.rolesconfig.event.AvaliableRolesEvent;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import br.com.muttley.security.server.repository.PassaportRepository;
import br.com.muttley.security.server.service.OwnerService;
import br.com.muttley.security.server.service.PassaportService;
import br.com.muttley.security.server.service.UserRolesView;
import com.mongodb.BasicDBObject;
import com.mongodb.DBRef;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static br.com.muttley.model.security.Role.ROLE_OWNER;
import static br.com.muttley.model.security.Role.ROLE_WORK_TEAM_CREATE;
import static br.com.muttley.model.security.Role.ROLE_WORK_TEAM_DELETE;
import static br.com.muttley.model.security.Role.ROLE_WORK_TEAM_READ;
import static br.com.muttley.model.security.Role.ROLE_WORK_TEAM_UPDATE;
import static br.com.muttley.model.security.rolesconfig.AvaliableRoles.newAvaliableRoles;
import static br.com.muttley.model.security.rolesconfig.AvaliableRoles.newViewRoleDefinition;
import static br.com.muttley.model.security.rolesconfig.FontSet.MDI;
import static br.com.muttley.model.security.rolesconfig.ViewRoleDefinition.newRoleDefinition;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Update.Position.FIRST;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Service do owner do odin
 */
@Service
public class PassaportServiceImpl extends SecurityServiceImpl<Passaport> implements PassaportService {
    private final PassaportRepository repository;
    private final UserRolesView userRolesView;
    private static final String[] basicRoles = new String[]{"passaport"};
    private final DocumentNameConfig documentNameConfig;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final OwnerService ownerService;
    private final LocalRolesService localRolesService;

    @Autowired
    public PassaportServiceImpl(final PassaportRepository repository, final UserRolesView userRolesView, final MongoTemplate template, final DocumentNameConfig documentNameConfig, final ApplicationEventPublisher applicationEventPublisher, final OwnerService ownerService, final LocalRolesService localRolesService) {
        super(repository, template, Passaport.class);
        this.repository = repository;
        this.userRolesView = userRolesView;
        this.documentNameConfig = documentNameConfig;
        this.applicationEventPublisher = applicationEventPublisher;
        this.ownerService = ownerService;
        this.localRolesService = localRolesService;
    }

    @Override
    public String[] getBasicRoles() {
        return basicRoles;
    }

    @Override
    public void beforeSave(final User user, final Passaport passaport) {
        //garantindo que não será alterado informações cruciais
        if (!(user.getCurrentOwner() == null && passaport.getOwner() != null)) {
            passaport.setOwner(user.getCurrentOwner());
        }
        //adicionando as roles de dependencias
        passaport.addRoles(this.loadAvaliableRoles(user).getDependenciesRolesFrom(passaport.getRoles()));
        super.beforeSave(user, passaport);
    }

    @Override
    public void checkPrecondictionSave(final User user, final Passaport passaport) {

        //verificando validando o owner
        //o evento irá verificar se foi informado o owner corretamente
        //pegando o owner da requisição atual, ou o owner já vindo no json caso seja uma requisição
        //do servidor odin
        this.applicationEventPublisher.publishEvent(new ValidateOwnerInWorkGroupEvent(user, passaport));

        //só podemo aceitar salvar um grupo pro owner caso ainda não exista um
        if (this.existPassaportForOwner(passaport) && passaport.containsRole(ROLE_OWNER)) {
            if (!this.isEmpty(user)) {
                throw new MuttleyBadRequestException(Passaport.class, "roles", "Não se pode existir mais de um grupo principal");
            }
        }
        final Map<String, Object> filter = new HashMap(2);
        filter.put("owner.$id", user.getCurrentOwner() == null ? passaport.getOwner().getObjectId() : user.getCurrentOwner().getObjectId());
        filter.put("userMaster", passaport.getUserMaster() == null ? user : passaport.getUserMaster());
        filter.put("name", passaport.getName());
        if (this.repository.exists(filter)) {
            throw new MuttleyBadRequestException(Passaport.class, "name", "Já existe um grupo de trabalho com este nome");
        }

        //validando usuário
        passaport.setMembers(
                passaport.getMembers()
                        .parallelStream()
                        .filter(it -> it.getId() != null && !"".equals(it.getUserName()))
                        .collect(toSet())
        );
    }

    @Override
    public void afterSave(final User user, final Passaport passaport) {
        this.expire(user, passaport);
    }

    @Override
    public void beforeUpdate(final User user, final Passaport passaport) {
        //garantindo que não será alterado informações cruciais
        passaport.setOwner(user.getCurrentOwner());
        passaport.addRoles(this.loadAvaliableRoles(user).getDependenciesRolesFrom(passaport.getRoles()));
        super.beforeUpdate(user, passaport);
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final Passaport passaport) {
        //não se pode alterar workteam que seja do owner
        if (this.existPassaportForOwner(passaport) && passaport.containsRole(ROLE_OWNER)) {
            throw new MuttleyBadRequestException(Passaport.class, "roles", "Não se pode editar o grupo principal");
        }
        //verificando se o workteam é do owner ou não
        final Passaport other = this.findById(user, passaport.getId());
        if (other.containsRole(ROLE_OWNER)) {
            throw new MuttleyBadRequestException(Passaport.class, "roles", "Não se pode editar o grupo principal");
        }
        //validando usuário
        passaport.setMembers(
                passaport.getMembers()
                        .parallelStream()
                        .filter(it -> it.getId() != null && !"".equals(it.getUserName()))
                        .collect(toSet())
        );
    }

    @Override
    public void afterUpdate(final User user, final Passaport passaport) {
        this.expire(user, passaport);
    }

    @Override
    public void beforeDelete(final User user, final Passaport passaport) {
        this.expire(user, passaport);
    }

    @Override
    public void checkPrecondictionDelete(final User user, final String id) {
        final Passaport passaport = this.findById(user, id);
        if (passaport.containsRole(ROLE_OWNER)) {
            throw new MuttleyBadRequestException(Passaport.class, "roles", "Não se pode excluir o grupo principal");
        }
        this.expire(user, passaport);
        super.checkPrecondictionDelete(user, id);
    }

    /*@Override
    public Long count(final User user, final Map<String, Object> allRequestParams) {
        return this.repository.count(user.getCurrentOwner());
    }*/

    @Override
    public List<Passaport> findAll(final User user, final Map<String, String> allRequestParams) {
        return this.findByUser(user);
    }

    @Override
    public Passaport findByName(final User user, final String name) {
        final Passaport cwt = repository.findByName(user.getCurrentOwner(), name);
        if (isNull(cwt)) {
            throw new MuttleyNotFoundException(Passaport.class, "name", "Registro não encontrado")
                    .addDetails("name", name);
        }
        return cwt;
    }

    @Override
    public List<Passaport> findByUserMaster(final Owner owner, final User user) {
        final List<Passaport> itens = repository.findByUserMaster(owner, user);
        if (CollectionUtils.isEmpty(itens)) {
            throw new MuttleyNoContentException(Passaport.class, "name", "Nenhum time de trabalho encontrado");
        }
        return itens;
    }

    @Override
    public List<Passaport> findByUser(final User user) {
        /**
         *db.getCollection("muttley-work-teams").aggregate([
         *    {$match:{$or:[{"userMaster.$id": ObjectId("5d49cca5a1d16f19595be983")}, {"members.$id":ObjectId("5d49cca5a1d16f19595be983")}]}},
         * ])
         */
        final AggregationResults<Passaport> passaportsResult = this.mongoTemplate.aggregate(
                newAggregation(
                        match(
                                new Criteria().orOperator(
                                        where("userMaster.$id").is(new ObjectId(user.getId())),
                                        where("members.$id").is(new ObjectId(user.getId()))
                                )
                        )
                )
                , Passaport.class, Passaport.class);
        if (passaportsResult == null) {
            throw new MuttleyNotFoundException(Passaport.class, "members", "Nenhum passaport encontrado para o usuário informado");
        }
        final List<Passaport> passaports = passaportsResult.getMappedResults();
        if (CollectionUtils.isEmpty(passaports)) {
            throw new MuttleyNotFoundException(Passaport.class, "members", "Nenhum passaport encontrado para o usuário informado");
        }
        return passaports;
    }

    @Override
    public Set<Role> loadCurrentRoles(final User user) {
        return this.userRolesView.findByUser(user);
    }

    @Override
    public AvaliableRoles loadAvaliableRoles(final User user) {
        final AvaliableRolesEvent event = new AvaliableRolesEvent(user,
                newAvaliableRoles(
                        newViewRoleDefinition("account-group-outline", MDI, "Times de trabalho", "Ações relacionada a times de trabalho",
                                newRoleDefinition(ROLE_WORK_TEAM_CREATE, "Inserir times de trabalho"),
                                newRoleDefinition(ROLE_WORK_TEAM_READ, "Visualizar times de trabalho"),
                                newRoleDefinition(ROLE_WORK_TEAM_UPDATE, "Atualizar times de trabalho"),
                                newRoleDefinition(ROLE_WORK_TEAM_DELETE, "Remover times de trabalho")
                        )
                )
        );

        this.applicationEventPublisher.publishEvent(event);

        return event.getSource();
    }

    @Override
    public void removeUserFromAllPassaport(Owner owner, User user) {
        this.mongoTemplate.updateMulti(
                new Query(
                        where("owner.$id").is(owner.getObjectId())
                ),
                new Update().pull("members", new BasicDBObject("$in", asList(
                        new DBRef(this.documentNameConfig.getNameCollectionUser(), user.getObjectId())
                ))),
                Passaport.class
        );
    }

    @Override
    public void addUserForPassaportIfNotExists(final User user, final Passaport passaport, final UserData userForAdd) {
        if (!this.userIsPresentInPassaport(user, passaport, userForAdd)) {
            this.mongoTemplate.updateMulti(
                    new Query(
                            where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                    .and("id").is(passaport.getObjectId())
                    ),
                    new Update().push("members").atPosition(FIRST).each(new DBRef(this.documentNameConfig.getNameCollectionUser(), new ObjectId(userForAdd.getId()))),
                    Passaport.class
            );
        }
    }

    @Override
    public boolean userIsPresentInPassaport(final User user, final Passaport passaport, final UserData userForCheck) {
        return this.userIsPresentInPassaport(user, passaport.getId(), userForCheck);
    }

    @Override
    public boolean userIsPresentInPassaport(final User user, final String idPassaport, final UserData userForCheck) {
        return this.mongoTemplate.exists(
                new Query(
                        where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                .and("id").is(new ObjectId(idPassaport))
                                .and("members").in(new DBRef(this.documentNameConfig.getNameCollectionUser(), new ObjectId(userForCheck.getId())))
                ),
                Passaport.class
        );
    }

    @Override
    public Passaport createPassaportFor(final User user, final String ownerId, final Passaport passaport) {
        final Owner owner = this.ownerService.findById(user, ownerId);
        passaport.setOwner(owner);
        passaport.setUserMaster(owner.getUserMaster());
        return this.save(owner.getUserMaster().setCurrentOwner(owner), passaport);
    }

    @Override
    public void configPassaports(final User user) {
        //criando grupo principal
        final Passaport passaport = new Passaport()
                .setName("Grupo principal")
                .setDescription("Grupo principal do sistema criado específicamente para dar autorizações de uso do usuário principal do sistema (Owner)")
                .setUserMaster(user)
                .addRole(ROLE_OWNER);

        //verificando se não existe workTeam para o Owner
        if (!existPassaportForOwner(passaport)) {


            passaport.setUserMaster(user);

            //criando grupo
        }


    }

    /**
     * Checando se existe grupo de trabalho para o Owner
     */
    private boolean existPassaportForOwner(final Passaport passaport) {
        /**
         * db.getCollection("muttley-passaports").aggregate([
         *     {$match:{
         *         owner: {'$ref' : 'muttley-owners', '$id' : ObjectId('5d07cece444c5b2ceb5e0942')},
         *         userMaster:{'$ref' : 'muttley-users', '$id' : ObjectId('5d07cada444c5b2ceb5e0940')},
         *         roles:{$elemMatch:{'roleName':'ROLE_OWNER'}}
         *     }},
         *     {
         *       $count: "count"
         *     }
         *     ])
         */
        final AggregationResults<UserViewServiceImpl.ResultCount> result = this.mongoTemplate.aggregate(
                newAggregation(
                        match(
                                //filtrando o owner
                                where("owner.$id").is(passaport.getOwner().getObjectId())
                                        //filtrando o usuário principal
                                        .and("userMaster.$id").is(new ObjectId(passaport.getUserMaster().getId()))
                                        //filtrando as roles
                                        .and("roles").elemMatch(
                                                new Criteria().is(ROLE_OWNER)
                                        )
                        ),
                        Aggregation.count().as("count")
                ), Passaport.class, UserViewServiceImpl.ResultCount.class
        );
        if (result == null || result.getUniqueMappedResult() != null) {
            return result.getUniqueMappedResult().getCount() > 0;
        }
        return false;
    }

    private void expire(final User user, final Passaport passaport) {
        this.localRolesService.expireRoles(passaport.getUserMaster());
        passaport.getMembers().forEach(m -> {
            this.localRolesService.expireRoles(m);
        });
    }
}
