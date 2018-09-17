package br.com.muttley.model.property;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Joel Rodrigues Moreira on 17/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@ConfigurationProperties(prefix = MuttleyModelProperty.PREFIX)
@Getter
@Setter
@Accessors(chain = true)
public class MuttleyModelProperty {
    protected final static String PREFIX = "muttley.model.security";
    private String ownerDocument = "muttley-owners";
    private String userDocument = "muttley-users";
    private String accessPlanDocument = "muttley-access-plans";
    private String userPreferenceDocument = "muttley-users-preferences";
    private String workTeamDocument = "muttley-work-teams";
}