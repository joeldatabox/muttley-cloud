package br.com.muttley.security.server.config.model;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 17/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration(value = "documentNameConfig")
public class DocumentNameConfig {
    private final String nameCollectionOwner;
    private final String nameCollectionUser;
    private final String nameCollectionAccessPlan;
    private final String nameCollectionUserPreferences;
    private final String nameCollectionWorkTeam;

    public DocumentNameConfig(
            @Value("${br.com.muttley.security.server.owner-document:muttley-owners}") final String nameCollectionOwner,
            @Value("${br.com.muttley.security.server.user-document:muttley-users}") final String nameCollectionUser,
            @Value("${br.com.muttley.security.server.access-plan-document:muttley-access-plans}") final String nameCollectionAccessPlan,
            @Value("${br.com.muttley.security.server.user-preference-document:muttley-users-preferences}") final String nameCollectionUserPreferences,
            @Value("${br.com.muttley.security.server.work-team-document:muttley-work-teams}") final String nameCollectionWorkTeam) {
        this.nameCollectionOwner = nameCollectionOwner;
        this.nameCollectionUser = nameCollectionUser;
        this.nameCollectionAccessPlan = nameCollectionAccessPlan;
        this.nameCollectionUserPreferences = nameCollectionUserPreferences;
        this.nameCollectionWorkTeam = nameCollectionWorkTeam;
    }

    public String getNameCollectionOwner() {
        return nameCollectionOwner;
    }

    public String getNameCollectionUser() {
        return nameCollectionUser;
    }

    public String getNameCollectionAccessPlan() {
        return nameCollectionAccessPlan;
    }

    public String getNameCollectionUserPreferences() {
        return nameCollectionUserPreferences;
    }

    public String getNameCollectionWorkTeam() {
        return nameCollectionWorkTeam;
    }
}

