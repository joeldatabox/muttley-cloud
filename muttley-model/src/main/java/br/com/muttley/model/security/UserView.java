package br.com.muttley.model.security;

import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetaDataDocument;
import br.com.muttley.model.jackson.converter.ListDocumentSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Objects;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 29/04/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserView implements Document {
    private String id;
    private String name;
    private String userName;
    private String email;
    private Set<String> nickUsers;
    @DBRef
    @JsonSerialize(using = ListDocumentSerializer.class)
    private Set<Owner> owners;
    private Historic historic;
    private MetaDataDocument metaData;

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

    public void setEmail(final String email) {
        this.email = email;
    }

    public Set<String> getNickUsers() {
        return nickUsers;
    }

    public void setNickUsers(final Set<String> nickUsers) {
        this.nickUsers = nickUsers;
    }

    public Set<Owner> getOwners() {
        return owners;
    }

    public UserView setOwners(final Set<Owner> owners) {
        this.owners = owners;
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
    public MetaDataDocument getMetaData() {
        return metaData;
    }

    @Override
    public UserView setMetaData(final MetaDataDocument metaData) {
        this.metaData = metaData;
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
