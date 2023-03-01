package br.com.muttley.model.workteam;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.domain.Domain;
import br.com.muttley.model.security.jackson.UserDeserializer;
import br.com.muttley.model.security.jackson.UserSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
    /*@JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)*/
    private final User user;
    private final Domain domain;

    @JsonCreator
    @PersistenceConstructor
    public WorkTeamMember(@JsonProperty("user") User user, @JsonProperty("domain") Domain domain) {
        this.user = user;
        this.domain = domain;
    }
}
