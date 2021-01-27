package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.security.server.events.ConfigFirstOwnerPreferenceEvent;
import br.com.muttley.security.server.service.UserRolesView;
import com.mongodb.BasicDBObject;
import org.bson.BsonString;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.Set;

import static br.com.muttley.model.security.preference.UserPreferences.OWNER_PREFERENCE;
import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 23/05/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class UserRolesViewImpl implements UserRolesView {
    private final MongoTemplate template;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public UserRolesViewImpl(final MongoTemplate mongoTemplate, final ApplicationEventPublisher eventPublisher) {
        this.template = mongoTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Set<Role> findByUser(final User user) {
        if (!user.containsPreference(OWNER_PREFERENCE)) {
            this.eventPublisher.publishEvent(new ConfigFirstOwnerPreferenceEvent(user));
        }

        /*final UserRolesViewResul result = this.template.findOne(new Query(
                where("owner.$id").is(user.getCurrentOwner().getObjectId())
                        .and("userId").is(new ObjectId(user.getId()))
        ), UserRolesViewResul.class, "view_muttley_work_teams_roles_user");
        if (result == null || result.isEmpty()) {
            throw new MuttleyNotFoundException(UserRolesViewResul.class, "userId", "Nenhuma role foi encontrada");
        }
        return result.getRoles();*/
        /**
         * db.getCollection("muttley-work-teams").aggregate([
         *     {$match:{
         *         "owner.$id":ObjectId("5e28b3e3637e580001e465d6"),
         *         $or:[
         *             {"userMaster.$id":ObjectId("5e28b392637e580001e465d4")},
         *             {"members.$id":ObjectId("5e28b392637e580001e465d4") }
         *         ]}
         *     },
         *     {$group:{_id:"$owner", roles:{$addToSet:"$roles"}}},
         *     {$project:{
         *         roles:{
         *             $reduce:{
         *                 input:"$roles",
         *                 initialValue:[],
         *                 in:{
         *                     $concatArrays:["$$value", "$$this"]
         *                 }
         *             }
         *         }
         *     }},
         *     {$unwind:"$roles"},
         *     {$group:{_id:"", roles:{$addToSet:"$roles"}}}
         * ])
         */
        final ObjectId idUser = new ObjectId(user.getId());
        final AggregationResults<UserRolesResult> result = this.template.aggregate(
                Aggregation.newAggregation(
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId()).orOperator(
                                where("userMaster.$id").is(idUser),
                                where("members.$id").is(idUser)
                        )),
                        group("$owner").addToSet("$roles").as("roles"),
                        project().and(context ->
                                new BasicDBObject("$reduce",
                                        new BasicDBObject("input", "$roles")
                                                .append("initialValue", asList())
                                                .append("in",
                                                        new BasicDBObject("$concatArrays", asList("$$value", "$$this"))
                                                )
                                )
                        ).as("roles").and(context -> new BasicDBObject("aux", new BsonString("1"))).as("aux"),
                        unwind("$roles"),
                        group("$aux").addToSet("roles").as("roles")
                ),
                WorkTeam.class,
                UserRolesResult.class
        );
        if (result == null || result.getUniqueMappedResult() == null) {
            throw new MuttleyNotFoundException(UserRolesResult.class, "userId", "Você está sem permissões de acesso. Para resolver isso contate o administrador do sistema");
        }
        return result.getUniqueMappedResult().getRoles();
    }

    private class UserRolesResult {
        Set<Role> roles;

        public Set<Role> getRoles() {
            return roles;
        }

        public UserRolesResult setRoles(final Set<Role> roles) {
            this.roles = roles;
            return this;
        }

        public boolean isEmpty() {
            return roles == null || roles.isEmpty();
        }
    }
}
