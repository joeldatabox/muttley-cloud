package br.com.muttley.model.security.model;

import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import com.google.common.base.Objects;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 07/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "user-preferences")
@CompoundIndexes({
        @CompoundIndex(name = "user_index_unique", def = "{'user' : 1}", unique = true)
})
public class UserPreferences implements Document<ObjectId> {
    @Id
    private ObjectId id;
    @DBRef
    private User user;
    private Historic historic;
    private Set<Preference> preferences;

    public UserPreferences() {
        this.preferences = new HashSet<>();
    }

    @Override
    public ObjectId getId() {
        return this.id;
    }

    @Override
    public UserPreferences setId(final ObjectId id) {
        this.id = id;
        return this;
    }

    @Override
    public UserPreferences setId(final String id) {
        return this.setId(new ObjectId(id));
    }

    @Override
    public UserPreferences setHistoric(final Historic historic) {
        this.historic = historic;
        return this;
    }

    @Override
    public Historic getHistoric() {
        return this.historic;
    }

    public boolean contains(final String key) {
        final Preference p = new Preference(key, null);
        return this.preferences.contains(p);
    }

    public User getUser() {
        return user;
    }

    public UserPreferences setUser(final User user) {
        this.user = user;
        return this;
    }

    public Set<Preference> getPreferences() {
        return preferences;
    }

    public UserPreferences setPreferences(final Set<Preference> preferences) {
        this.preferences = preferences;
        return this;
    }

    public Preference get(final String key) {
        return this.preferences.stream()
                .filter(p -> p.key.equals(key))
                .findAny()
                .get();
    }

    public UserPreferences set(final Preference preference) {
        this.preferences.add(preference);
        return this;
    }

    public UserPreferences set(final String key, final Object value) {
        this.preferences.add(new Preference(key, value));
        return this;
    }
}

class Preference {
    protected String key;
    protected Object value;

    public Preference() {
    }

    public Preference(final String key, final Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Preference)) return false;
        final Preference that = (Preference) o;
        return Objects.equal(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(key, 2, 3);
    }
}
