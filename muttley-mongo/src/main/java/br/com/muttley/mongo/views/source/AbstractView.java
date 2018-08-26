package br.com.muttley.mongo.views.source;

import org.springframework.data.annotation.Id;

/**
 * @author Joel Rodrigues Moreira on 18/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstractView {
    @Id
    protected String id;
    protected String name;
    protected String version;
    protected String description;

    public AbstractView() {
    }

    public AbstractView(final String name, final String version, String description) {
        this();
        this.name = name;
        this.version = version;
        this.description = description;
    }

    public AbstractView(final ViewSource source) {
        this(source.getViewName(), source.getVersion(), source.getDescription());
    }

    public String getId() {
        return id;
    }

    public AbstractView setId(final String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public AbstractView setName(final String name) {
        this.name = name;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public AbstractView setVersion(final String version) {
        this.version = version;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public AbstractView setDescription(final String description) {
        this.description = description;
        return this;
    }

    public AbstractView updateInfo(final ViewSource source) {
        setDescription(source.getDescription())
                .setVersion(source.getVersion());
        return this;
    }
}
