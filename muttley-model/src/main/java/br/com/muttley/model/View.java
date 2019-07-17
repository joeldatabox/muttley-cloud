package br.com.muttley.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

/**
 * @author Joel Rodrigues Moreira on 15/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = "_view")
@CompoundIndexes({
        @CompoundIndex(name = "name_index_unique", def = "{'name' : 1}", unique = true)
})
@TypeAlias("view")
public class View {
    @Id
    private String id;
    private String name;
    private String version;
    private String description;

    public View() {
    }

    public View(final String name, final String version, String description) {
        this();
        this.name = name;
        this.version = version;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public View setId(final String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public View setName(final String name) {
        this.name = name;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public View setVersion(final String version) {
        this.version = version;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public View setDescription(final String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof View)) return false;
        final View view = (View) o;
        return Objects.equals(getId(), view.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
