package br.com.muttley.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

/**
 * @author Joel Rodrigues Moreira on 30/01/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class TimeZoneDocument {
    @Transient
    @JsonIgnore
    private static final String PATTERN = "(([+-]|)([01]?[0-9]|2[0-3]):[0-5][0-9])|(([+-]|)([01]?[0-9]|2[0-3])([0-5][0-9]))";

    @Getter
    @Setter
    private String originCreate;
    @Getter
    @Setter
    private String serverCreate;

    @Getter
    @Setter
    private String originLastUpdate;
    @Getter
    @Setter
    private String serverLastUpdate;

    public TimeZoneDocument() {
    }

    @JsonCreator
    public TimeZoneDocument(
            @JsonProperty("originCreate") final String originCreate,
            @JsonProperty("serverCreate") final String serverCreate,
            @JsonProperty("originLastUpdate") final String originLastUpdate,
            @JsonProperty("serverLastUpdate") final String serverLastUpdate) {
        this.originCreate = originCreate;
        this.serverCreate = serverCreate;
        this.originLastUpdate = originLastUpdate;
        this.serverLastUpdate = serverLastUpdate;
    }
}
