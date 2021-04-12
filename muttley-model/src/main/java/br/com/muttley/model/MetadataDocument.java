package br.com.muttley.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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

    public MetadataDocument() {
        this.timeZones = new TimeZoneDocument();
        this.versionDocument = new VersionDocument();
    }

    @JsonCreator
    public MetadataDocument(@JsonProperty("timeZones") final TimeZoneDocument timeZones, @JsonProperty("versionDocument") final VersionDocument versionDocument) {
        this.timeZones = timeZones;
        this.versionDocument = versionDocument;
    }

    public boolean containsTimeZones() {
        return this.getTimeZones() != null;
    }

    public boolean containsVersionDocument() {
        return this.getVersionDocument() != null;
    }

    public static class Builder {
        private TimeZoneDocument timeZone;
        private VersionDocument version;

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

        public MetadataDocument build() {
            return new MetadataDocument(this.timeZone, this.version);
        }
    }

}
