package br.com.muttley.model.workteam;

import br.com.muttley.model.security.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 08/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class WorkTeamDomain {
    /* @JsonSerialize(using = UserSerializer.class)
     @JsonDeserialize(using = UserDeserializer.class)*/
    private User userMaster;
    /*@JsonSerialize(using = UserCollectionSerializer.class)
    @JsonDeserialize(using = UserSetDeserializer.class)*/
    private Set<WorkTeamMember> members;
}
