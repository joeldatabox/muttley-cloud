package br.com.muttley.security.server.config.model;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 17/04/18.
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
    private final String nameCollectionAdminUserBase;
    private final String nameCollectionUserBase;
    private final String nameCollectionAccessPlan;
    private final String nameCollectionUserPreferences;
    private final String nameCollectionUserTokensNotification;
    private final String nameCollectionAdminPassaport;
    private final String nameCollectionPassaport;
    private final String nameCollectionWorkTeam;
    private final String nameCollectionUserDataBinding;
    private final String nameViewCollectionUser;
    private final String nameViewCollectionPassaport;

    private final String nameCollectionAPIToken;
    private final String nameViewCollectionPassaportRolesUser;

    private final String nameViewCollectionWorkTeam;

    public DocumentNameConfig(
            @Value("${br.com.muttley.security.server.owner-document:muttley-admin-owners}") final String nameCollectionAdminOwner,
            @Value("${br.com.muttley.security.server.owner-document:muttley-owners}") final String nameCollectionOwner,
            @Value("${br.com.muttley.security.server.user-document:muttley-users}") final String nameCollectionUser,
            @Value("${br.com.muttley.security.server.user-password-document:muttley-users-password}") final String nameCollectionPassword,
            @Value("${br.com.muttley.security.server.user-base-document:muttley-admin-users-base}") final String nameCollectionAdminUserBase,
            @Value("${br.com.muttley.security.server.user-base-document:muttley-users-base}") final String nameCollectionUserBase,
            @Value("${br.com.muttley.security.server.access-plan-document:muttley-access-plans}") final String nameCollectionAccessPlan,
            @Value("${br.com.muttley.security.server.user-preference-document:muttley-users-preferences}") final String nameCollectionUserPreferences,
            @Value("${br.com.muttley.security.server.user-tokens-notification-document:muttley-users-tokens-notification}") final String nameCollectionUserTokensNotification,
            @Value("${br.com.muttley.security.server.admin-passaport-document:muttley-admin-passaports}") final String nameCollectionAdminPassaport,
            @Value("${br.com.muttley.security.server.passaport-document:muttley-passaports}") final String nameCollectionPassaport,
            @Value("${br.com.muttley.security.server.work-team-document:muttley-work-teams}") final String nameCollectionWorkTeam,
            @Value("${br.com.muttley.security.server.user-data-binding:muttley-users-databinding}") final String nameCollectionUserDataBinding,
            @Value("${br.com.muttley.security.server.user-document-view:view-muttley-users}") final String nameViewCollectionUser,
            @Value("${br.com.muttley.security.server.passaport-document-view:view-muttley-passaports}") final String nameViewCollectionPassaport,
            @Value("${br.com.muttley.security.server.api-token-document:muttley-api-token}") final String nameCollectionAPIToken,
            @Value("${br.com.muttley.security.server.passaport-role-document-view:view-muttley-passaports-roles-user}") final String nameViewCollectionPassaportRolesUser,
            @Value("${br.com.muttley.security.server.work-team-document-view:view-muttley-work-teams}") final String nameViewCollectionWorkTeam
    ) {
        this.nameCollectionAdminOwner = nameCollectionAdminOwner;
        this.nameCollectionOwner = nameCollectionOwner;
        this.nameCollectionUser = nameCollectionUser;
        this.nameCollectionPassword = nameCollectionPassword;
        this.nameCollectionAdminUserBase = nameCollectionAdminUserBase;
        this.nameCollectionUserBase = nameCollectionUserBase;
        this.nameCollectionAccessPlan = nameCollectionAccessPlan;
        this.nameCollectionUserPreferences = nameCollectionUserPreferences;
        this.nameCollectionUserTokensNotification = nameCollectionUserTokensNotification;
        this.nameCollectionAdminPassaport = nameCollectionAdminPassaport;
        this.nameCollectionPassaport = nameCollectionPassaport;
        this.nameCollectionWorkTeam = nameCollectionWorkTeam;
        this.nameCollectionUserDataBinding = nameCollectionUserDataBinding;
        this.nameViewCollectionUser = nameViewCollectionUser;
        this.nameViewCollectionPassaport = nameViewCollectionPassaport;
        this.nameCollectionAPIToken = nameCollectionAPIToken;
        this.nameViewCollectionPassaportRolesUser = nameViewCollectionPassaportRolesUser;
        this.nameViewCollectionWorkTeam = nameViewCollectionWorkTeam;
    }
}

