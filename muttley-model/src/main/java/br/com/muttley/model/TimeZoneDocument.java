package br.com.muttley.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.Transient;

/**
 * @author Joel Rodrigues Moreira on 13/05/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class TimeZoneDocument {
    @Transient
    @JsonIgnore
    private static final String PATTERN = "(([+-]|)([01]?[0-9]|2[0-3]):[0-5][0-9])|(([+-]|)([01]?[0-9]|2[0-3])([0-5][0-9]))";
    private String originCreate;
    private String serverCreate;
    private String originLastUpdate;
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
