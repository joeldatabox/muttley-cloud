package br.com.muttley.model.workteam;

import br.com.muttley.model.security.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.PersistenceConstructor;

/**
 * @author Joel Rodrigues Moreira on 22/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@EqualsAndHashCode(of = "user")
public class WorkTeamMember {
    private final User user;
    private final boolean canEdit;

    @JsonCreator
    @PersistenceConstructor
    public WorkTeamMember(@JsonProperty("user") User user, @JsonProperty("canEdit") Boolean canEdit) {
        this.user = user;
        this.canEdit = canEdit == null ? false : canEdit;
    }
}
