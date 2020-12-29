package br.com.muttley.model.security;

import br.com.muttley.model.security.jackson.UserDataDeserializer;
import br.com.muttley.model.security.jackson.UserDataSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

/**
 * @author Joel Rodrigues Moreira 29/12/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
public class OwnerDataImpl implements OwnerData {
    private final String id;
    private final String name;
    private final String description;
    @JsonSerialize(using = UserDataSerializer.class)
    @JsonDeserialize(using = UserDataDeserializer.class)
    private final UserData userMaster;

    public OwnerDataImpl(final String id, final String name, final String description, final UserData userMaster) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userMaster = userMaster;
    }

    public OwnerDataImpl(final OwnerData owner) {
        this(owner.getId(), owner.getName(), owner.getDescription(), owner.getUserMaster());
    }
}
