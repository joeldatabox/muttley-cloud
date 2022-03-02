package br.com.muttley.hermes.server.config.model;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 30/04/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration(value = "documentNameConfig")
public class DocumentNameConfig {
    private final String nameCollectionOwner;
    private final String nameCollectionUser;
    private final String nameCollectionUserBase;
    private final String nameCollectionAccessPlan;
    private final String nameCollectionUserPreferences;
    private final String nameCollectionUserTokensNotification;
    private final String nameCollectionPassaport;
    private final String nameCollectionUserDataBinding;

    public DocumentNameConfig(
            @Value("${br.com.muttley.security.server.owner-document:muttley-owners}") final String nameCollectionOwner,
            @Value("${br.com.muttley.security.server.user-document:muttley-users}") final String nameCollectionUser,
            @Value("${br.com.muttley.security.server.user-base-document:muttley-users-base}") final String nameCollectionUserBase,
            @Value("${br.com.muttley.security.server.access-plan-document:muttley-access-plans}") final String nameCollectionAccessPlan,
            @Value("${br.com.muttley.security.server.user-preference-document:muttley-users-preferences}") final String nameCollectionUserPreferences,
            @Value("${br.com.muttley.security.server.user-tokens-notification-document:muttley-users-tokens-notification}") final String nameCollectionUserTokensNotification,
            @Value("${br.com.muttley.security.server.passaport-document:muttley-passaports}") final String nameCollectionPassaport,
            @Value("${br.com.muttley.security.server.user-data-binding:muttley-users-databinding}") final String nameCollectionUserDataBinding) {
        this.nameCollectionOwner = nameCollectionOwner;
        this.nameCollectionUser = nameCollectionUser;
        this.nameCollectionUserBase = nameCollectionUserBase;
        this.nameCollectionAccessPlan = nameCollectionAccessPlan;
        this.nameCollectionUserPreferences = nameCollectionUserPreferences;
        this.nameCollectionUserTokensNotification = nameCollectionUserTokensNotification;
        this.nameCollectionPassaport = nameCollectionPassaport;
        this.nameCollectionUserDataBinding = nameCollectionUserDataBinding;
    }

    public String getNameCollectionOwner() {
        return nameCollectionOwner;
    }

    public String getNameCollectionUser() {
        return nameCollectionUser;
    }

    public String getNameCollectionUserBase() {
        return nameCollectionUserBase;
    }

    public String getNameCollectionAccessPlan() {
        return nameCollectionAccessPlan;
    }

    public String getNameCollectionUserPreferences() {
        return nameCollectionUserPreferences;
    }

    public String getNameCollectionUserTokensNotification() {
        return this.nameCollectionUserTokensNotification;
    }

    public String getNameCollectionPassaport() {
        return nameCollectionPassaport;
    }

    public String getNameCollectionUserDataBinding() {
        return nameCollectionUserDataBinding;
    }
}
