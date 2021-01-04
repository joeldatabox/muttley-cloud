package br.com.muttley.model.security;

import br.com.muttley.model.security.jackson.UserDataDeserializer;
import br.com.muttley.model.security.jackson.UserDataSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Joel Rodrigues Moreira 29/12/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class OwnerDataImpl implements OwnerData {
    private String id;
    private String name;
    private String description;
    @JsonSerialize(using = UserDataSerializer.class)
    @JsonDeserialize(using = UserDataDeserializer.class)
    private UserData userMaster;

    public OwnerDataImpl() {
    }

    @JsonCreator
    public OwnerDataImpl(
            @JsonProperty("id") final String id,
            @JsonProperty("name") final String name,
            @JsonProperty("description") final String description,
            @JsonProperty("userMaster") final UserData userMaster) {
        this();
        this.id = id;
        this.name = name;
        this.description = description;
        this.userMaster = userMaster;
    }

    public OwnerDataImpl(final OwnerData owner) {
        this(owner.getId(), owner.getName(), owner.getDescription(), owner.getUserMaster());
    }
}
