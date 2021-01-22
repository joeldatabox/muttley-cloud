package br.com.muttley.model.security;

import br.com.muttley.model.security.jackson.UserDataDeserializer;
import br.com.muttley.model.security.jackson.UserDataSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Joel Rodrigues Moreira 04/12/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@EqualsAndHashCode(of = "user")
public class UserBaseItem {
    @DBRef
    @NotNull(message = "Informe o usuário que está efetuando essa operação")
    @JsonSerialize(using = UserDataSerializer.class)
    @JsonDeserialize(using = UserDataDeserializer.class)
    private UserData addedBy;

    @DBRef
    @NotNull(message = "Informe o usuário participante da base")
    @JsonSerialize(using = UserDataSerializer.class)
    @JsonDeserialize(using = UserDataDeserializer.class)
    private UserData user;

    @NotNull
    private Date dtCreate;

    private boolean status;

    public UserBaseItem() {
        this.status = true;
    }

    @JsonCreator
    public UserBaseItem(
            @JsonProperty("addedBy") final UserData addedBy,
            @JsonProperty("user") final UserData user,
            @JsonProperty("dtCreate") final Date dtCreate,
            @JsonProperty("status") final boolean status) {
        this();
        this.addedBy = addedBy;
        this.user = user;
        this.dtCreate = dtCreate;
        this.status = status;
    }
}