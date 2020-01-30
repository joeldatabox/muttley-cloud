package br.com.muttley.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Joel Rodrigues Moreira on 30/01/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
public class MetaDataDocument {
    private TimeZoneDocument timeZones;
    private VersionDocument versionDocument;

    public MetaDataDocument() {
    }

    @JsonCreator
    public MetaDataDocument(@JsonProperty("timeZones") final TimeZoneDocument timeZones, @JsonProperty("versionDocument") final VersionDocument versionDocument) {
        this.timeZones = timeZones;
        this.versionDocument = versionDocument;
    }
}
