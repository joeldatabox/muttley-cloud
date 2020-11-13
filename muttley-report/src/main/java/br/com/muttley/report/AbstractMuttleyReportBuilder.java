package br.com.muttley.report;

import br.com.muttley.headers.components.MuttleyCurrentTimezone;
import br.com.muttley.headers.components.MuttleyCurrentVersion;
import br.com.muttley.model.security.User;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira 10/11/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractMuttleyReportBuilder<T extends MuttleyReportBuilder> implements MuttleyReportBuilder<T> {
    protected Map<String, Object> params = new HashMap<>();
    protected MongoTemplate template;
    protected User user;
    protected MuttleyCurrentVersion version;
    protected MuttleyCurrentTimezone timezone;
    private final T INSTANCE = (T) this;

    public T addParam(final String key, final Object value) {
        this.params.put(key, value);
        return this.INSTANCE;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public T setTemplate(final MongoTemplate template) {
        this.template = template;
        return this.INSTANCE;
    }

    public MongoTemplate getTemplate() {
        return this.template;
    }

    public T setUser(final User user) {
        this.user = user;
        if (user != null) {
            this.addParam("CURRENT_USER", user.getName());
        }
        return this.INSTANCE;
    }

    public User getUser() {
        return user;
    }

    public T setCurrentVersion(final MuttleyCurrentVersion version) {
        this.version = version;
        this.addParam("CURRENT_VERSION", version.getCurrenteFromServer());
        return this.INSTANCE;
    }

    public T setCurrentTimezone(final MuttleyCurrentTimezone timezone) {
        this.timezone = timezone;
        this.addParam("CURRENT_TIMEZONE", timezone.getCurrentTimezoneFromRequestOrServer());
        return this.INSTANCE;
    }
}
