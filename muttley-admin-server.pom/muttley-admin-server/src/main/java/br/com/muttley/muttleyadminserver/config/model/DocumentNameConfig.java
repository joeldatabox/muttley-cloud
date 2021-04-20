package br.com.muttley.muttleyadminserver.config.model;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 30/04/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration(value = "documentNameConfig")
@Getter
public class DocumentNameConfig {
    private final String nameCollectionAdminOwner;
    private final String nameCollectionOwner;
    private final String nameCollectionUser;
    private final String nameCollectionPassword;
    private final String nameCollectionUserBase;
    private final String nameCollectionAccessPlan;
    private final String nameCollectionUserPreferences;
    private final String nameCollectionUserTokensNotification;
    private final String nameCollectionAdminWorkTeam;
    private final String nameCollectionWorkTeam;
    private final String nameCollectionUserDataBinding;
    private final String nameViewCollectionUser;
    private final String nameViewCollectionWorkTeam;
    private final String nameViewCollectionWorkTeamRolesUser;

    public DocumentNameConfig(
            @Value("${br.com.muttley.security.server.owner-document:muttley-admin-owners}") final String nameCollectionAdminOwner,
            @Value("${br.com.muttley.security.server.owner-document:muttley-owners}") final String nameCollectionOwner,
            @Value("${br.com.muttley.security.server.user-document:muttley-users}") final String nameCollectionUser,
            @Value("${br.com.muttley.security.server.user-password-document:muttley-users-password}") final String nameCollectionPassword,
            @Value("${br.com.muttley.security.server.user-base-document:muttley-users-base}") final String nameCollectionUserBase,
            @Value("${br.com.muttley.security.server.access-plan-document:muttley-access-plans}") final String nameCollectionAccessPlan,
            @Value("${br.com.muttley.security.server.user-preference-document:muttley-users-preferences}") final String nameCollectionUserPreferences,
            @Value("${br.com.muttley.security.server.user-tokens-notification-document:muttley-users-tokens-notification}") final String nameCollectionUserTokensNotification,
            @Value("${br.com.muttley.security.server.work-team-document:muttley-admin-work-teams}") final String nameCollectionAdminWorkTeam,
            @Value("${br.com.muttley.security.server.work-team-document:muttley-work-teams}") final String nameCollectionWorkTeam,
            @Value("${br.com.muttley.security.server.user-data-binding:muttley-users-databinding}") final String nameCollectionUserDataBinding,
            @Value("${br.com.muttley.security.server.user-document-view:view-muttley-users}") final String nameViewCollectionUser,
            @Value("${br.com.muttley.security.server.work-team-document-view:view-muttley-work-teams}") final String nameViewCollectionWorkTeam,
            @Value("${br.com.muttley.security.server.work-team-document-view:view-muttley-work-teams-roles-user}") final String nameViewCollectionWorkTeamRolesUser) {
        this.nameCollectionAdminOwner = nameCollectionAdminOwner;
        this.nameCollectionOwner = nameCollectionOwner;
        this.nameCollectionUser = nameCollectionUser;
        this.nameCollectionPassword = nameCollectionPassword;
        this.nameCollectionUserBase = nameCollectionUserBase;
        this.nameCollectionAccessPlan = nameCollectionAccessPlan;
        this.nameCollectionUserPreferences = nameCollectionUserPreferences;
        this.nameCollectionUserTokensNotification = nameCollectionUserTokensNotification;
        this.nameCollectionAdminWorkTeam = nameCollectionAdminWorkTeam;
        this.nameCollectionWorkTeam = nameCollectionWorkTeam;
        this.nameCollectionUserDataBinding = nameCollectionUserDataBinding;
        this.nameViewCollectionUser = nameViewCollectionUser;
        this.nameViewCollectionWorkTeam = nameViewCollectionWorkTeam;
        this.nameViewCollectionWorkTeamRolesUser = nameViewCollectionWorkTeamRolesUser;
    }
}
