package br.com.muttley.model.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Joel Rodrigues Moreira on 17/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@ConfigurationProperties(prefix = MuttleyModelProperty.PREFIX)
public class MuttleyModelProperty {
    protected final static String PREFIX = "muttley.model.security";
    private String ownerDocument;
    private String userDocument;
    private String accessPlanDocument;
    private String userPreferenceDocument;
    private String workTeamDocument;

    public MuttleyModelProperty() {
        this.ownerDocument = "muttley-owners";
        this.userDocument = "muttley-users";
        this.accessPlanDocument = "muttley-access-plans";
        this.userPreferenceDocument = "muttley-users-preferences";
        this.workTeamDocument = "muttley-work-teams";
    }

    public String getOwnerDocument() {
        return ownerDocument;
    }

    public MuttleyModelProperty setOwnerDocument(String ownerDocument) {
        this.ownerDocument = ownerDocument;
        return this;
    }

    public String getUserDocument() {
        return userDocument;
    }

    public MuttleyModelProperty setUserDocument(String userDocument) {
        this.userDocument = userDocument;
        return this;
    }

    public String getAccessPlanDocument() {
        return accessPlanDocument;
    }

    public MuttleyModelProperty setAccessPlanDocument(String accessPlanDocument) {
        this.accessPlanDocument = accessPlanDocument;
        return this;
    }

    public String getUserPreferenceDocument() {
        return userPreferenceDocument;
    }

    public MuttleyModelProperty setUserPreferenceDocument(String userPreferenceDocument) {
        this.userPreferenceDocument = userPreferenceDocument;
        return this;
    }

    public String getWorkTeamDocument() {
        return workTeamDocument;
    }

    public MuttleyModelProperty setWorkTeamDocument(String workTeamDocument) {
        this.workTeamDocument = workTeamDocument;
        return this;
    }
}
