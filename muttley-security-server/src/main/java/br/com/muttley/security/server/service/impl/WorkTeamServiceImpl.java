package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.autoconfig.DocumentNameConfig;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.model.security.rolesconfig.AvaliableRoles;
import br.com.muttley.model.security.rolesconfig.event.AvaliableRolesEvent;
import br.com.muttley.security.server.repository.WorkTeamRepository;
import br.com.muttley.security.server.service.UserRolesViewService;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
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

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Service do owner do odin
 */
@Service
public class WorkTeamServiceImpl extends SecurityServiceImpl<WorkTeam> implements WorkTeamService {
    private final WorkTeamRepository repository;
    private final UserRolesViewService userRolesView;
    private static final String[] basicRoles = new String[]{"work_team"};
    private final MongoTemplate mongoTemplate;
    private final DocumentNameConfig documentNameConfig;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public WorkTeamServiceImpl(final WorkTeamRepository repository, final UserRolesViewService userRolesView, final MongoTemplate template, final DocumentNameConfig documentNameConfig, final ApplicationEventPublisher applicationEventPublisher) {
        super(repository, WorkTeam.class);
        this.repository = repository;
        this.userRolesView = userRolesView;
        this.mongoTemplate = template;
        this.documentNameConfig = documentNameConfig;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public String[] getBasicRoles() {
        return basicRoles;
    }

    @Override
    public void beforeSave(final User user, final WorkTeam workTeam) {
        //garantindo que não será alterado informações cruciais
        workTeam.setOwner(user.getCurrentOwner());
        super.beforeSave(user, workTeam);
    }

    @Override
    public void checkPrecondictionSave(final User user, final WorkTeam workTeam) {
        //só podemo aceitar salvar um grupo pro owner caso ainda não exista um
        if (workTeam.containsRole(ROLE_OWNER)) {
            if (!this.isEmpty(user)) {
                throw new MuttleyBadRequestException(WorkTeam.class, "roles", "Não se pode existir mais de um grupo principal");
            }
        }
        final Map<String, Object> filter = new HashMap(2);
        filter.put("owner.$id", user.getCurrentOwner().getObjectId());
        filter.put("userMaster", workTeam.getUserMaster());
        filter.put("name", workTeam.getName());
        if (this.repository.exists(filter)) {
            throw new MuttleyBadRequestException(WorkTeam.class, "name", "Já existe um grupo de trabalho com este nome");
        }
    }

    @Override
    public void beforeUpdate(final User user, final WorkTeam workTeam) {
        //garantindo que não será alterado informações cruciais
        workTeam.setOwner(user.getCurrentOwner());
        super.beforeUpdate(user, workTeam);
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final WorkTeam workTeam) {
        //não se pode alterar workteam que seja do owner
        if (workTeam.containsRole(ROLE_OWNER)) {
            throw new MuttleyBadRequestException(WorkTeam.class, "roles", "Não se pode editar o grupo principal");
        }
        //verificando se o workteam é do owner ou não
        final WorkTeam other = this.findById(user, workTeam.getId());
        if (other.containsRole(ROLE_OWNER)) {
            throw new MuttleyBadRequestException(WorkTeam.class, "roles", "Não se pode editar o grupo principal");
        }

    }

    @Override
    public void checkPrecondictionDelete(final User user, final String id) {
        if (this.findById(user, id).containsRole(ROLE_OWNER)) {
            throw new MuttleyBadRequestException(WorkTeam.class, "roles", "Não se pode excluir o grupo principal");
        }
        super.checkPrecondictionDelete(user, id);
    }

    @Override
    public WorkTeam findByName(final Owner owner, final String name) {
        final WorkTeam cwt = repository.findByName(owner, name);
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

    /*@Override
    public WorkTeam findOwnerGroup(final User user) {
        *//**
     db.getCollection("muttley-work-teams")
     .aggregate([
     {$match:{"owner.$id":ObjectId("5cdb05cbc2183f60addb972c")}},
     {$unwind:"$roles"},
     {$match:{ "roles":{roleName:"ROLE_OWNER"}}},
     {$project:{_id:1}},
     {$lookup:{
     from:"muttley-work-teams",
     localField:"_id",
     foreignField: "_id",
     as:"result"
     }},
     {$unwind:"$result"},
     {$project:{"_id":"$result._id", "_class":"$result._class", "name":"$result.name", "description":"$result.description","historic":"$result.historic", "userMaster":"$result.userMaster","owner":"$result.owner", "members":"$result.members", "roles":"$result.roles"}}
     ])
     *//*
        this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId())),
                        unwind("$roles"),
                        match(where("roles").is(ROLE_OWNER)),
                        project("_id"),
                        lookup(documentNameConfig.getNameCollectionWorkTeam(), "_id", "_id", "result"),
                        unwind("$result"),
                        project()
                                .and("_id").as("$result._id")
                                .and("_class").as("$result._class")
                        .and("name")
                ), "", WorkTeam.class
        )
    }*/
}
