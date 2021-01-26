package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.model.Document;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.repository.UserPreferencesRepository;
import br.com.muttley.security.server.service.UserPreferencesService;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira 08/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class UserPreferencesImpl implements UserPreferencesService {
    private final UserPreferencesRepository repository;
    private final MongoTemplate template;

    @Autowired
    public UserPreferencesImpl(final UserPreferencesRepository repository, final MongoTemplate template) {
        this.repository = repository;
        this.template = template;
    }

    @Override
    public UserPreferences createPreferencesFor(final User user) {
        //devemos garantir que cada usuário tenha no máximo um UserPreference
        if (this.existsUserPreferencesFor(user)) {
            throw new MuttleyBadRequestException(UserPreferences.class, "user", "Já existe preferencias cadastradas para esse usuário");
        }
        return this.repository.save(new UserPreferences().setUser(user));
    }

    @Override
    public void save(final User user, final UserPreferences preferences) {
        //devemos garantir que cada usuário tenha no máximo um UserPreference
        if (this.existsUserPreferencesFor(user)) {
            throw new MuttleyBadRequestException(UserPreferences.class, "user", "Já existe preferencias cadastradas para esse usuário");
        }
        if (preferences.getUser() == null) {
            preferences.setUser(user);
        } else if (!user.equals(preferences.getUser())) {
            throw new MuttleyBadRequestException(UserPreferences.class, "user", "Somente o proprio usuário pode criar suas preferencias");
        }

        this.repository.save(preferences);
    }

    @Override
    public void setPreference(final User user, final Preference preference) {
        if (!this.existsUserPreferencesFor(user)) {
            //se não tiver UserPreferences, vamos criar um novo
            this.save(user, new UserPreferences().setUser(user).set(preference));
        } else {
            this.template.updateFirst(
                    new Query(where("user.$id").is(new ObjectId(user.getId()))),
                    new Update().pull("preferences", new BasicDBObject("key", preference.getKey())),
                    UserPreferences.class
            );

            this.template.updateFirst(
                    new Query(where("user.$id").is(new ObjectId(user.getId()))),
                    new Update().addToSet("preferences", preference),
                    UserPreferences.class
            );
        }
    }

    @Override
    public void setPreference(final User user, final String key, final String value) {
        this.setPreference(user, new Preference(key, value));
    }

    @Override
    public void setPreference(final User user, final String key, final Document value) {
        this.setPreference(user, new Preference(key, value.getId()));
    }

    @Override
    public Preference getPreference(final User user, final String key) {
        /**
         * db.getCollection("muttley-users-preferences").aggregate([
         *     {$match:{"user.$id" : ObjectId("5e28b392637e580001e465d4"), "preferences.key":"OWNER_PREFERENCE"}},
         *     {$project:{preferences:1}},
         *     {$unwind:"$preferences"},
         *     {$match:{"preferences.key":"OWNER_PREFERENCE"}}
         * ])
         */
        final AggregationResults<Preference> results = this.template.aggregate(
                newAggregation(
                        match(where("user.$id").is(new ObjectId(user.getId())).and("preferences.key").is(key)),
                        project("preferences"),
                        unwind("$preferences"),
                        match(where("preferences.key").is(key)),
                        replaceRoot("preferences")
                ),
                UserPreferences.class,
                Preference.class);
        if (results != null && results.getUniqueMappedResult() != null) {
            return results.getUniqueMappedResult();
        }
        return null;
    }

    @Override
    public String getPreferenceValue(final User user, final String key) {
        final Preference preference = this.getPreference(user, key);
        return preference == null ? null : (String) preference.getValue();
    }

    @Override
    public void removePreference(final User user, final String key) {
        this.template.updateFirst(
                new Query(where("user.$id").is(new ObjectId(user.getId()))),
                new Update().pull("preferences", new BasicDBObject("key", key)),
                UserPreferences.class
        );
    }

    @Override
    public UserPreferences getUserPreferences(final User user) {
        return repository.findByUser(user);
    }

    @Override
    public boolean containsPreference(final User user, final String key) {
        return repository.exists("user.$id", new ObjectId(user.getId()), "preferences.key", key);
    }

    private boolean existsUserPreferencesFor(final User user) {
        return repository.exists("user.$id", new ObjectId(user.getId()));
    }
}
