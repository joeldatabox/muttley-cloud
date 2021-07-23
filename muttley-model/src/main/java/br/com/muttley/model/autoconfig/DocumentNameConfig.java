package br.com.muttley.model.autoconfig;

import br.com.muttley.model.property.MuttleyModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira on 17/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Component
public class DocumentNameConfig {
    private final MuttleyModelProperty properties;

    public DocumentNameConfig(@Autowired MuttleyModelProperty properties) {
        this.properties = properties;
    }

    public String getNameCollectionOwner() {
        return properties.getOwnerDocument();
    }

    public String getNameCollectionUser() {
        return properties.getUserDocument();
    }

    public String getNameCollectionUserBase() {
        return properties.getUserBaseDocument();
    }

    public String getNameCollectionAccessPlan() {
        return properties.getAccessPlanDocument();
    }

    public String getNameCollectionUserPreferences() {
        return properties.getUserPreferenceDocument();
    }

    public String getNameCollectionUsersTokensNotification() {
        return properties.getUsersTokensNotification();
    }

    public String getNameCollectionWorkTeam() {
        return properties.getWorkTeamDocument();
    }
}
