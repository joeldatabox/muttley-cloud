package br.com.muttley.model.security.preference;

import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.security.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.springframework.util.Assert.notNull;

/**
 * @author Joel Rodrigues Moreira on 07/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "#{@documentNameConfig.getNameCollectionUserPreferences()}")
@CompoundIndexes({
        @CompoundIndex(name = "user_index_unique", def = "{'user' : 1}", unique = true)
})
@TypeAlias("muttley-users-preferences")
@Accessors(chain = true)
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class UserPreferences implements Document {
    public static final String WORK_TEAM_PREFERENCE = "WorkTeamPreference";
    @Id
    private String id;
    @JsonManagedReference
    @JsonIgnore
    @DBRef
    private User user;
    private Historic historic;
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
            @JsonProperty("id") String id,
            @JsonProperty("user") User user,
            @JsonProperty("historic") Historic historic,
            @JsonProperty("preferences") Set<Preference> preferences) {
        this.id = id;
        this.user = user;
        this.historic = historic;
        this.preferences = preferences;
    }

    public boolean contains(final String key) {
        final Preference p = new Preference(key, null);
        return this.preferences.contains(p);
    }


    public Preference get(final String key) {
        return this.preferences.stream()
                .filter(p -> p.key.equals(key))
                .findAny()
                .get();
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
