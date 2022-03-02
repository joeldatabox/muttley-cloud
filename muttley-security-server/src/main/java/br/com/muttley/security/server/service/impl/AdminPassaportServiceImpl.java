package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.localcache.services.LocalRolesService;
import br.com.muttley.model.admin.AdminOwner;
import br.com.muttley.model.admin.AdminPassaport;
import br.com.muttley.model.security.Passaport;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.ValidateOwnerInWorkGroupEvent;
import br.com.muttley.model.security.rolesconfig.AvaliableRoles;
import br.com.muttley.model.security.rolesconfig.event.AvaliableRolesEvent;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import br.com.muttley.security.server.repository.AdminPassaportRepository;
import br.com.muttley.security.server.service.AdminOwnerService;
import br.com.muttley.security.server.service.AdminPassaportService;
import br.com.muttley.security.server.service.UserRolesView;
import com.mongodb.BasicDBObject;
import com.mongodb.DBRef;
import org.bson.types.ObjectId;
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
import static br.com.muttley.model.security.Role.ROLE_PASSAPORT_CREATE;
import static br.com.muttley.model.security.Role.ROLE_PASSAPORT_DELETE;
import static br.com.muttley.model.security.Role.ROLE_PASSAPORT_READ;
import static br.com.muttley.model.security.Role.ROLE_PASSAPORT_UPDATE;
import static br.com.muttley.model.security.rolesconfig.AvaliableRoles.newAvaliableRoles;
import static br.com.muttley.model.security.rolesconfig.AvaliableRoles.newViewRoleDefinition;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class AdminPassaportServiceImpl extends SecurityServiceImpl<AdminPassaport> implements AdminPassaportService {
    private final AdminPassaportRepository repository;
    private final UserRolesView userRolesView;
    private static final String[] basicRoles = new String[]{"passaport"};
    private final DocumentNameConfig documentNameConfig;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final AdminOwnerService ownerService;
    private final LocalRolesService localRolesService;


    public AdminPassaportServiceImpl(
            final AdminPassaportRepository repository,
            final UserRolesView userRolesView,
            final MongoTemplate template,
            final DocumentNameConfig documentNameConfig,
            final ApplicationEventPublisher applicationEventPublisher,
            final AdminOwnerService ownerService,
            final LocalRolesService localRolesService) {
        super(repository, template, AdminPassaport.class);
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
    public void beforeSave(final User user, final AdminPassaport passaport) {
        //garantindo que não será alterado informações cruciais
        if (!(user.getCurrentOwner() == null && passaport.getOwner() != null)) {
            passaport.setOwner(user.getCurrentOwner());
        }
        //adicionando as roles de dependencias
        passaport.addRoles(this.loadAvaliableRoles(user).getDependenciesRolesFrom(passaport.getRoles()));
        super.beforeSave(user, passaport);
    }

    @Override
    public void checkPrecondictionSave(final User user, final AdminPassaport passaport) {

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
    public void afterSave(final User user, final AdminPassaport passaport) {
        this.expire(user, passaport);
    }

    @Override
    public void beforeUpdate(final User user, final AdminPassaport passaport) {
        //garantindo que não será alterado informações cruciais
        passaport.setOwner(user.getCurrentOwner());
        passaport.addRoles(this.loadAvaliableRoles(user).getDependenciesRolesFrom(passaport.getRoles()));
        super.beforeUpdate(user, passaport);
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final AdminPassaport passaport) {
        //não se pode alterar workteam que seja do owner
        if (this.existPassaportForOwner(passaport) && passaport.containsRole(ROLE_OWNER)) {
            throw new MuttleyBadRequestException(Passaport.class, "roles", "Não se pode editar o grupo principal");
        }
        //verificando se o workteam é do owner ou não
        final AdminPassaport other = this.findById(user, passaport.getId());
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
    public void afterUpdate(final User user, final AdminPassaport passaport) {
        this.expire(user, passaport);
    }

    @Override
    public void beforeDelete(final User user, final AdminPassaport passaport) {
        this.expire(user, passaport);
    }

    @Override
    public void checkPrecondictionDelete(final User user, final String id) {
        final AdminPassaport passaport = this.findById(user, id);
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
    public List<AdminPassaport> findAll(final User user, final Map<String, String> allRequestParams) {
        return this.findByUser(user);
    }

    @Override
    public AdminPassaport findByName(final User user, final String name) {
        final AdminPassaport cwt = repository.findByName(user.getCurrentOwner(), name);
        if (isNull(cwt)) {
            throw new MuttleyNotFoundException(Passaport.class, "name", "Registro não encontrado")
                    .addDetails("name", name);
        }
        return cwt;
    }

    @Override
    public List<AdminPassaport> findByUserMaster(final AdminOwner owner, final User user) {
        final List<AdminPassaport> itens = repository.findByUserMaster(owner, user);
        if (CollectionUtils.isEmpty(itens)) {
            throw new MuttleyNoContentException(Passaport.class, "name", "Nenhum time de trabalho encontrado");
        }
        return itens;
    }

    @Override
    public List<AdminPassaport> findByUser(final User user) {
        /**
         *db.getCollection("muttley-work-teams").aggregate([
         *    {$match:{$or:[{"userMaster.$id": ObjectId("5d49cca5a1d16f19595be983")}, {"members.$id":ObjectId("5d49cca5a1d16f19595be983")}]}},
         * ])
         */
        final AggregationResults<AdminPassaport> passaportsResult = this.mongoTemplate.aggregate(
                newAggregation(
                        match(
                                new Criteria().orOperator(
                                        where("userMaster.$id").is(new ObjectId(user.getId())),
                                        where("members.$id").is(new ObjectId(user.getId()))
                                )
                        )
                )
                , AdminPassaport.class, AdminPassaport.class);
        if (passaportsResult == null) {
            throw new MuttleyNotFoundException(AdminPassaport.class, "members", "Nenhum passaport encontrado para o usuário informado");
        }
        final List<AdminPassaport> passaports = passaportsResult.getMappedResults();
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
                        newViewRoleDefinition("Times de trabalho", "Ações relacionada a times de trabalho", ROLE_PASSAPORT_CREATE, ROLE_PASSAPORT_READ, ROLE_PASSAPORT_UPDATE, ROLE_PASSAPORT_DELETE)
                )
        );

        this.applicationEventPublisher.publishEvent(event);

        return event.getSource();
    }

    @Override
    public void removeUserFromAllPassaport(final AdminOwner owner, final User user) {
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
    public AdminPassaport createPassaportFor(final User user, final String ownerId, final AdminPassaport passaport) {
        final AdminOwner owner = this.ownerService.findById(user, ownerId);
        passaport.setOwner(owner);
        passaport.setUserMaster(owner.getUserMaster());
        //this.checkPrecondictionSave(owner.getUserMaster(), workTeam);
        return passaport;
    }

    @Override
    public void configPassaports(final User user) {
        //criando grupo principal
        final AdminPassaport passaport = (AdminPassaport) new AdminPassaport()
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
    private boolean existPassaportForOwner(final AdminPassaport passaport) {
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
                ), AdminPassaport.class, UserViewServiceImpl.ResultCount.class
        );
        if (result == null || result.getUniqueMappedResult() != null) {
            return result.getUniqueMappedResult().getCount() > 0;
        }
        return false;
    }

    private void expire(final User user, final AdminPassaport passaport) {
        this.localRolesService.expireRoles(passaport.getUserMaster());
        passaport.getMembers().forEach(m -> {
            this.localRolesService.expireRoles(m);
        });
    }
}
