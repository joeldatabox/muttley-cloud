package br.com.muttley.model.security.preference;

import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.security.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.springframework.util.Assert.notNull;

/**
 * @author Joel Rodrigues Moreira on 07/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "#{documentNameConfig.getNameCollectionUserPreferences()}")
@CompoundIndexes({
        @CompoundIndex(name = "user_index_unique", def = "{'user' : 1}", unique = true),
        @CompoundIndex(name = "preferences_key_index", def = "{'preferences.key': 1}"),
        @CompoundIndex(name = "preferences_key_value_index", def = "{'preferences.key': 1, 'preferences.value':1}")
})
public class UserPreferences implements Document {
    public static final String OWNER_PREFERENCE = "OWNER_PREFERENCE";
    @Id
    private String id;
    @JsonIgnore
    @DBRef
    private User user;
    private MetadataDocument metadata;
    private Set<Preference> preferences;

    public UserPreferences() {
        this.preferences = new HashSet<>();
    }

    public UserPreferences(final Preference... preferences) {
        notNull(preferences, "preferences is null");
        this.preferences = new HashSet<>();
        Stream.of(preferences)
                .map(p -> {
                    if (!p.isValid()) {
                        throw new IllegalArgumentException("key não pode ser nulla ou vazia");
                    }
                    return p;
                }).forEach(p -> set(p));
    }

    @JsonCreator
    public UserPreferences(
            final @JsonProperty("id") String id,
            final @JsonProperty("user") User user,
            final @JsonProperty("preferences") Set<Preference> preferences,
            final @JsonProperty("metadata") MetadataDocument metadata) {
        this.id = id;
        this.user = user;
        this.preferences = preferences;
        this.metadata = metadata;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public UserPreferences setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public MetadataDocument getMetadata() {
        return metadata;
    }

    @Override
    public UserPreferences setMetadata(final MetadataDocument metaData) {
        this.metadata = metaData;
        return this;
    }

    public boolean contains(final String key) {
        final Preference p = new Preference(key, null);
        return this.preferences.contains(p);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return CollectionUtils.isEmpty(this.preferences);
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
        return this.preferences.parallelStream()
                .filter(p -> p.key.equals(key))
                .findAny()
                .orElse(null);
    }

    public UserPreferences set(final Preference preference) {
        this.remove(preference.getKey());
        this.preferences.add(preference);
        return this;
    }

    public UserPreferences set(final String key, final Object value) {
        this.remove(key);
        this.preferences.add(new Preference(key, value));
        return this;
    }

    public UserPreferences set(final String key, final Document value) {
        this.remove(key);
        this.preferences.add(new Preference(key, value.getId()));
        return this;
    }

    public UserPreferences remove(final String key) {
        if (this.preferences.contains(new Preference(key, ""))) {
            this.preferences.remove(new Preference(key, ""));
        }
        return this;
    }
}
