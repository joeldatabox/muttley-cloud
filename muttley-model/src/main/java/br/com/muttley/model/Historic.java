package br.com.muttley.model;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.jackson.UserSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 31/01/18.
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class Historic {
    @JsonSerialize(using = UserSerializer.class)
    @DBRef
    private User createdBy;
    private Date dtCreate;
    @DBRef
    private User lastChangeBy;
    private Date dtChange;

    public Historic() {
    }

    @JsonCreator
    public Historic(
            @JsonProperty("createdBy") final User createdBy,
            @JsonProperty("dtCreate") final Date dtCreate,
            @JsonProperty("lastChangeBy") final User lastChangeBy,
            @JsonProperty("dtChange") final Date dtChange) {
        this();
        this.createdBy = createdBy;
        this.dtCreate = dtCreate;
        this.lastChangeBy = lastChangeBy;
        this.dtChange = dtChange;
    }
}
