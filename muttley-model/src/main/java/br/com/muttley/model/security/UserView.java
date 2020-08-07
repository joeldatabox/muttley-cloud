package br.com.muttley.model.security;

import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.jackson.converter.ListDocumentSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * @author Joel Rodrigues Moreira on 15/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class UserView implements Document {
    private String id;
    private String name;
    private String description;
    private String userName;
    private String email;
    private Set<String> nickUsers;
    @DBRef
    @JsonSerialize(using = ListDocumentSerializer.class)
    private Set<Owner> owners;
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
                .setOwners(user.getWorkTeams());
    }

    public UserView setOwners(final Set<Owner> owners) {
        this.owners = owners;
        return this;
    }

    public UserView setOwners(final Collection<WorkTeam> workTeams) {
        if (!CollectionUtils.isEmpty(workTeams)) {
            this.setOwners(
                    workTeams.stream()
                            .map(WorkTeam::getOwner)
                            .collect(toSet())
            );
        }
        return this;
    }
}

