package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.model.security.events.ValidateOwnerInWorkGroupEvent;
import br.com.muttley.model.security.rolesconfig.AvaliableRoles;
import br.com.muttley.model.security.rolesconfig.event.AvaliableRolesEvent;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import br.com.muttley.security.server.repository.WorkTeamRepository;
import br.com.muttley.security.server.service.OwnerService;
import br.com.muttley.security.server.service.UserRolesView;
import br.com.muttley.security.server.service.WorkTeamService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
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
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Service do owner do odin
 */
@Service
public class WorkTeamServiceImpl extends SecurityServiceImpl<WorkTeam> implements WorkTeamService {
    private final WorkTeamRepository repository;
    private final UserRolesView userRolesView;
    private static final String[] basicRoles = new String[]{"work_team"};
    private final DocumentNameConfig documentNameConfig;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final OwnerService ownerService;

    @Autowired
    public WorkTeamServiceImpl(final WorkTeamRepository repository, final UserRolesView userRolesView, final MongoTemplate template, final DocumentNameConfig documentNameConfig, final ApplicationEventPublisher applicationEventPublisher, final OwnerService ownerService) {
        super(repository, template, WorkTeam.class);
        this.repository = repository;
        this.userRolesView = userRolesView;
        this.documentNameConfig = documentNameConfig;
        this.applicationEventPublisher = applicationEventPublisher;
        this.ownerService = ownerService;
    }

    @Override
    public String[] getBasicRoles() {
        return basicRoles;
    }

    @Override
    public void beforeSave(final User user, final WorkTeam workTeam) {
        //garantindo que não será alterado informações cruciais
        if (!(user.getCurrentOwner() == null && workTeam.getOwner() != null)) {
            workTeam.setOwner(user.getCurrentOwner());
        }
        //adicionando as roles de dependencias
        workTeam.addRoles(this.loadAvaliableRoles(user).getDependenciesRolesFrom(workTeam.getRoles()));
        super.beforeSave(user, workTeam);
    }

    @Override
    public void checkPrecondictionSave(final User user, final WorkTeam workTeam) {

        //verificando validando o owner
        //o evento irá verificar se foi informado o owner corretamente
        //pegando o owner da requisição atual, ou o owner já vindo no json caso seja uma requisição
        //do servidor odin
        this.applicationEventPublisher.publishEvent(new ValidateOwnerInWorkGroupEvent(user, workTeam));

        //só podemo aceitar salvar um grupo pro owner caso ainda não exista um
        if (this.existWorkTeamForOwner(workTeam) && workTeam.containsRole(ROLE_OWNER)) {
            if (!this.isEmpty(user)) {
                throw new MuttleyBadRequestException(WorkTeam.class, "roles", "Não se pode existir mais de um grupo principal");
            }
        }
        final Map<String, Object> filter = new HashMap(2);
        filter.put("owner.$id", user.getCurrentOwner() == null ? workTeam.getOwner().getObjectId() : user.getCurrentOwner().getObjectId());
        filter.put("userMaster", workTeam.getUserMaster() == null ? user : workTeam.getUserMaster());
        filter.put("name", workTeam.getName());
        if (this.repository.exists(filter)) {
            throw new MuttleyBadRequestException(WorkTeam.class, "name", "Já existe um grupo de trabalho com este nome");
        }

        //validando usuário
        workTeam.setMembers(
                workTeam.getMembers()
                        .parallelStream()
                        .filter(it -> it.getId() != null && !"".equals(it.getUserName()))
                        .collect(toSet())
        );
    }

    @Override
    public void beforeUpdate(final User user, final WorkTeam workTeam) {
        //garantindo que não será alterado informações cruciais
        workTeam.setOwner(user.getCurrentOwner());
        workTeam.addRoles(this.loadAvaliableRoles(user).getDependenciesRolesFrom(workTeam.getRoles()));
        super.beforeUpdate(user, workTeam);
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final WorkTeam workTeam) {
        //não se pode alterar workteam que seja do owner
        if (this.existWorkTeamForOwner(workTeam) && workTeam.containsRole(ROLE_OWNER)) {
            throw new MuttleyBadRequestException(WorkTeam.class, "roles", "Não se pode editar o grupo principal");
        }
        //verificando se o workteam é do owner ou não
        final WorkTeam other = this.findById(user, workTeam.getId());
        if (other.containsRole(ROLE_OWNER)) {
            throw new MuttleyBadRequestException(WorkTeam.class, "roles", "Não se pode editar o grupo principal");
        }
        //validando usuário
        workTeam.setMembers(
                workTeam.getMembers()
                        .parallelStream()
                        .filter(it -> it.getId() != null && !"".equals(it.getUserName()))
                        .collect(toSet())
        );
    }

    @Override
    public void checkPrecondictionDelete(final User user, final String id) {
        if (this.findById(user, id).containsRole(ROLE_OWNER)) {
            throw new MuttleyBadRequestException(WorkTeam.class, "roles", "Não se pode excluir o grupo principal");
        }
        super.checkPrecondictionDelete(user, id);
    }

    /*@Override
    public Long count(final User user, final Map<String, Object> allRequestParams) {
        return this.repository.count(user.getCurrentOwner());
    }*/

    @Override
    public List<WorkTeam> findAll(final User user, final Map<String, String> allRequestParams) {
        return this.findByUser(user);
    }

    @Override
    public WorkTeam findByName(final User user, final String name) {
        final WorkTeam cwt = repository.findByName(user.getCurrentOwner(), name);
        if (isNull(cwt)) {
            throw new MuttleyNotFoundException(WorkTeam.class, "name", "Registro não encontrado")
                    .addDetails("name", name);
        }
        return cwt;
    }

    @Override
    public List<WorkTeam> findByUserMaster(final Owner owner, final User user) {
        final List<WorkTeam> itens = repository.findByUserMaster(owner, user);
        if (CollectionUtils.isEmpty(itens)) {
            throw new MuttleyNoContentException(WorkTeam.class, "name", "Nenhum time de trabalho encontrado");
        }
        return itens;
    }

    @Override
    public List<WorkTeam> findByUser(final User user) {
        /**
         *db.getCollection("muttley-work-teams").aggregate([
         *    {$match:{$or:[{"userMaster.$id": ObjectId("5d49cca5a1d16f19595be983")}, {"members.$id":ObjectId("5d49cca5a1d16f19595be983")}]}},
         * ])
         */
        final AggregationResults<WorkTeam> workTeamsResult = this.mongoTemplate.aggregate(
                newAggregation(
                        match(
                                new Criteria().orOperator(
                                        where("userMaster.$id").is(new ObjectId(user.getId())),
                                        where("members.$id").is(new ObjectId(user.getId()))
                                )
                        )
                )
                , WorkTeam.class, WorkTeam.class);
        if (workTeamsResult == null) {
            throw new MuttleyNotFoundException(WorkTeam.class, "members", "Nenhum workteam encontrado para o usuário informado");
        }
        final List<WorkTeam> workTeams = workTeamsResult.getMappedResults();
        if (CollectionUtils.isEmpty(workTeams)) {
            throw new MuttleyNotFoundException(WorkTeam.class, "members", "Nenhum workteam encontrado para o usuário informado");
        }
        return workTeams;
    }

    @Override
    public Set<Role> loadCurrentRoles(final User user) {
        return this.userRolesView.findByUser(user);
    }

    @Override
    public AvaliableRoles loadAvaliableRoles(final User user) {
        final AvaliableRolesEvent event = new AvaliableRolesEvent(user,
                newAvaliableRoles(
                        newViewRoleDefinition("Times de trabalho", "Ações relacionada a times de trabalho", ROLE_WORK_TEAM_CREATE, ROLE_WORK_TEAM_READ, ROLE_WORK_TEAM_UPDATE, ROLE_WORK_TEAM_DELETE)
                )
        );

        this.applicationEventPublisher.publishEvent(event);

        return event.getSource();
    }

    @Override
    public WorkTeam createWorkTeamFor(final User user, final String ownerId, final WorkTeam workTeam) {
        final Owner owner = this.ownerService.findById(user, ownerId);
        workTeam.setOwner(owner);
        workTeam.setUserMaster(owner.getUserMaster());
        //this.checkPrecondictionSave(owner.getUserMaster(), workTeam);
        return workTeam;
    }

    @Override
    public void configWorkTeams(final User user) {
        //criando grupo principal
        final WorkTeam workTeam = new WorkTeam()
                .setName("Grupo principal")
                .setDescription("Grupo principal do sistema criado específicamente para dar autorizações de uso do usuário principal do sistema (Owner)")
                .setUserMaster(user)
                .addRole(ROLE_OWNER);

        //verificando se não existe workTeam para o Owner
        if (!existWorkTeamForOwner(workTeam)) {


            workTeam.setUserMaster(user);

            //criando grupo
        }


    }

    /**
     * Checando se existe grupo de trabalho para o Owner
     */
    private boolean existWorkTeamForOwner(final WorkTeam workTeam) {
        /**
         * db.getCollection("muttley-work-teams").aggregate([
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
                                where("owner.$id").is(workTeam.getOwner().getObjectId())
                                        //filtrando o usuário principal
                                        .and("userMaster.$id").is(new ObjectId(workTeam.getUserMaster().getId()))
                                        //filtrando as roles
                                        .and("roles").elemMatch(
                                        new Criteria().is(ROLE_OWNER)
                                )
                        ),
                        Aggregation.count().as("count")
                ), WorkTeam.class, UserViewServiceImpl.ResultCount.class
        );
        if (result == null || result.getUniqueMappedResult() != null) {
            return result.getUniqueMappedResult().getCount() > 0;
        }
        return false;
    }
}
