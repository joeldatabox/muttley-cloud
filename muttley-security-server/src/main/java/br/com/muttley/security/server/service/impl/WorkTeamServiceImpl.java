package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import br.com.muttley.security.server.repository.WorkTeamRepository;
import br.com.muttley.security.server.service.UserRolesView;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

import static br.com.muttley.model.security.Role.ROLE_OWNER;
import static java.util.Objects.isNull;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
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
    private final MongoTemplate mongoTemplate;
    private final DocumentNameConfig documentNameConfig;

    @Autowired
    public WorkTeamServiceImpl(final WorkTeamRepository repository, final UserRolesView userRolesView, final MongoTemplate template, final DocumentNameConfig documentNameConfig) {
        super(repository, WorkTeam.class);
        this.repository = repository;
        this.userRolesView = userRolesView;
        this.mongoTemplate = template;
        this.documentNameConfig = documentNameConfig;
    }

    @Override
    public String[] getBasicRoles() {
        return basicRoles;
    }

    @Override
    public void checkPrecondictionSave(final User user, final WorkTeam value) {
        if
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final WorkTeam workTeam) {
        if (workTeam.containsRole(ROLE_OWNER)) {
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
    public WorkTeam findOwnerGroup(final User user) {
        /**
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
         */
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
    }
}
