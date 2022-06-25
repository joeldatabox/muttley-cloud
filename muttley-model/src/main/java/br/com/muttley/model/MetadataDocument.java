package br.com.muttley.model;

import br.com.muttley.model.security.User;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.PersistenceConstructor;

/**
 * @author Joel Rodrigues Moreira on 30/01/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class MetadataDocument {
    private TimeZoneDocument timeZones;
    private VersionDocument versionDocument;
    private Historic historic;

    public MetadataDocument(final User user) {
        this.timeZones = new TimeZoneDocument();
        this.versionDocument = new VersionDocument();
        this.historic = Historic.Builder.createNew(user);
    }

    @JsonCreator
    @PersistenceConstructor
    public MetadataDocument(
            @JsonProperty("timeZones") final TimeZoneDocument timeZones,
            @JsonProperty("versionDocument") final VersionDocument versionDocument,
            @JsonProperty("historic") final Historic historic) {
        this.timeZones = timeZones;
        this.versionDocument = versionDocument;
        this.historic = historic;
    }

    public boolean containsTimeZones() {
        return this.getTimeZones() != null;
    }

    public boolean containsVersionDocument() {
        return this.getVersionDocument() != null;
    }

    public boolean containsHistoric() {
        return this.getHistoric() != null;
    }

    public static class Builder {
        private TimeZoneDocument timeZone;
        private VersionDocument version;
        private Historic historic;

        public static Builder getInstance() {
            return new Builder();
        }

        public Builder setTimeZone(final TimeZoneDocument timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        public Builder setVersion(final VersionDocument version) {
            this.version = version;
            return this;
        }

        public Builder setHistoric(Historic historic) {
            this.historic = historic;
            return this;
        }

        public MetadataDocument build() {
            return new MetadataDocument(this.timeZone, this.version, historic);
        }
    }

}
