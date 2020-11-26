package br.com.muttley.model.security;

import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.jackson.converter.ListDocumentSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * @author Joel Rodrigues Moreira on 29/04/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
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

    @Override
    public String getId() {
        return id;
    }

    @Override
    public UserView setId(final String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserView setName(final String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public UserView setDescription(final String description) {
        this.description = description;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public UserView setUserName(final String userName) {
        this.userName = userName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserView setEmail(final String email) {
        this.email = email;
        return this;
    }

    public Set<String> getNickUsers() {
        return nickUsers;
    }

    public UserView setNickUsers(final Set<String> nickUsers) {
        this.nickUsers = nickUsers;
        return this;
    }

    public Set<Owner> getOwners() {
        return owners;
    }

    public UserView setOwners(final Set<Owner> owners) {
        this.owners = owners;
        return this;
    }

    public UserView setOwners(final Collection<WorkTeam> workTeams) {
        if (!CollectionUtils.isEmpty(workTeams)) {
            this.setOwners(
                    workTeams.parallelStream()
                            .map(WorkTeam::getOwner)
                            .collect(toSet())
            );
        }
        return this;
    }

    @Override
    public Historic getHistoric() {
        return historic;
    }

    @Override
    public UserView setHistoric(final Historic historic) {
        this.historic = historic;
        return this;
    }

    @Override
    public MetadataDocument getMetadata() {
        return metadata;
    }

    @Override
    public UserView setMetadata(final MetadataDocument metaData) {
        this.metadata = metaData;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof UserView)) return false;
        final UserView userView = (UserView) o;
        return Objects.equals(getId(), userView.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
