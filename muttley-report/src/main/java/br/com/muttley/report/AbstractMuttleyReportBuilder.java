package br.com.muttley.report;

import br.com.muttley.model.security.User;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira 10/11/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractMuttleyReportBuilder implements MuttleyReportBuilder {
    private Map<String, Object> params = new HashMap<>();
    private MongoTemplate template;
    private User user;

    public AbstractMuttleyReportBuilder addParam(final String key, final Object value) {
        this.params.put(key, value);
        return this;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public AbstractMuttleyReportBuilder setTemplate(final MongoTemplate template) {
        this.template = template;
        return this;
    }

    public MongoTemplate getTemplate() {
        return this.template;
    }

    public AbstractMuttleyReportBuilder setUser(final User user) {
        this.user = user;
        return this;
    }

    public User getUser() {
        return user;
    }
}
