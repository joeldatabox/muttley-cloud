package br.com.muttley.report;

import br.com.muttley.headers.components.MuttleyCurrentTimezone;
import br.com.muttley.headers.components.MuttleyCurrentVersion;
import br.com.muttley.model.security.User;
import lombok.Getter;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira 10/11/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractMuttleyReportBuilder<T extends MuttleyReportBuilder> implements MuttleyReportBuilder<T> {

    public static final String CURRENT_USER = "CURRENT_USER";
    public static final String CURRENT_VERSION = "CURRENT_VERSION";
    public static final String CURRENT_TIMEZONE = "CURRENT_TIMEZONE";

    @Getter
    protected Map<String, Object> params = new HashMap<>();
    @Getter
    protected MongoTemplate template;
    @Getter
    protected User currentUser;
    @Getter
    protected MuttleyCurrentVersion currentVersion;
    @Getter
    protected MuttleyCurrentTimezone currentTimezone;

    protected final T INSTANCE = (T) this;

    protected AbstractMuttleyReportBuilder() {
    }

    protected AbstractMuttleyReportBuilder(final MuttleyReportBuilder builder) {
        this.setTemplate(builder.getTemplate());
        this.setCurrentUser(builder.getCurrentUser());
        this.setCurrentTimezone(builder.getCurrentTimezone());
        this.setCurrentVersion(builder.getCurrentVersion());
    }

    public T addParam(final String key, final Object value) {
        this.params.put(key, value);
        return this.INSTANCE;
    }

    public T removeParam(final String key) {
        this.params.remove(key);
        return this.INSTANCE;
    }

    public T setTemplate(final MongoTemplate template) {
        this.template = template;
        return this.INSTANCE;
    }


    public T setCurrentUser(final User user) {
        this.currentUser = user;
        if (user != null) {
            this.addParam(CURRENT_USER, user.getName());
        }
        return this.INSTANCE;
    }

    public T setCurrentVersion(final MuttleyCurrentVersion version) {
        this.currentVersion = version;
        this.addParam(CURRENT_VERSION, version.getCurrenteFromServer());
        return this.INSTANCE;
    }

    public T setCurrentTimezone(final MuttleyCurrentTimezone timezone) {
        this.currentTimezone = timezone;
        this.addParam(CURRENT_TIMEZONE, timezone.getCurrentTimezoneFromRequestOrServer());
        return this.INSTANCE;
    }
}
