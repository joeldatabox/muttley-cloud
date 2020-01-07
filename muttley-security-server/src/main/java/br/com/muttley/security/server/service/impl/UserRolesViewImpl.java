package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.security.server.events.ConfigFirstWorkTeamEvent;
import br.com.muttley.security.server.service.UserRolesView;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Set;

import static br.com.muttley.model.security.preference.UserPreferences.WORK_TEAM_PREFERENCE;
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
        if (!user.containsPreference(WORK_TEAM_PREFERENCE)) {
            this.eventPublisher.publishEvent(new ConfigFirstWorkTeamEvent(user));
        }

        final UserRolesViewResul result = this.template.findOne(new Query(
                where("owner.$id").is(user.getCurrentOwner().getObjectId())
                        .and("userId").is(new ObjectId(user.getId()))
        ), UserRolesViewResul.class, "view_muttley_work_teams_roles_user");
        if (result == null || result.isEmpty()) {
            throw new MuttleyNotFoundException(UserRolesViewResul.class, "userId", "Nenhuma role foi encontrada");
        }
        return result.getRoles();
    }

    private class UserRolesViewResul {
        Set<Role> roles;

        public Set<Role> getRoles() {
            return roles;
        }

        public UserRolesViewResul setRoles(final Set<Role> roles) {
            this.roles = roles;
            return this;
        }

        public boolean isEmpty() {
            return roles == null || roles.isEmpty();
        }
    }
}
