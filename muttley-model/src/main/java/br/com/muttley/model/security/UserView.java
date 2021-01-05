package br.com.muttley.model.security;

import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 29/04/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class UserView implements UserData, Document {
    private String id;
    private String name;
    private String description;
    private String userName;
    private String email;
    private Set<String> nickUsers;
    private boolean status;
    @DBRef
    @JsonIgnore
    private Owner owner;
    private Historic historic;
    private MetadataDocument metadata;

    public UserView() {
    }

    public UserView(final User user) {
        this.setId(user.getId())
                .setName(user.getName())
                .setDescription(user.getDescription())
                .setUserName(user.getUserName())
                .setEmail(user.getEmail())
                .setNickUsers(user.getNickUsers())
                .setOwner(user.getCurrentOwner());
    }

    private UserView setOwner(final WorkTeam workTeam) {
        if (workTeam != null) {
            this.setOwner(workTeam.getOwner());
        }
        return this;
    }

    public UserView setOwner(final Owner owner) {
        this.owner = owner;
        return this;
    }
}
