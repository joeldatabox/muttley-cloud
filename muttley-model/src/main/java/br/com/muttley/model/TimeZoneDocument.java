package br.com.muttley.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static br.com.muttley.utils.TimeZoneUtils.getTimezoneFromId;
import static br.com.muttley.utils.TimeZoneUtils.isValidTimeZone;

/**
 * @author Joel Rodrigues Moreira on 30/01/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class TimeZoneDocument {

    /**
     * Deve conter informações do timezone corrente do registro
     */
    private String currentTimeZone;

    /**
     * Deve conter informações do timezone de criação do registro
     */
    private String createTimeZone;

    /**
     * Deve conter informações do timezone corrente do registro por parte do servidor
     */
    private String serverCurrentTimeZone;

    /**
     * Deve conter informações do timezone de criação do registro por parte do servidor
     */
    private String serverCreteTimeZone;

    public TimeZoneDocument() {
    }

    @JsonCreator
    public TimeZoneDocument(
            @JsonProperty("currentTimeZone") final String currentTimeZone,
            @JsonProperty("createTimeZone") final String createTimeZone,
            @JsonProperty("serverCurrentTimeZone") final String serverCurrentTimeZone,
            @JsonProperty("serverCreteTimeZone") final String serverCreteTimeZone) {
        this.setCurrentTimeZone(currentTimeZone);
        this.setCreateTimeZone(createTimeZone);
        this.setServerCurrentTimeZone(serverCurrentTimeZone);
        this.setServerCreteTimeZone(serverCreteTimeZone);
    }

    public TimeZoneDocument setCurrentTimeZone(final String currentTimeZone) {
        this.currentTimeZone = getTimezoneFromId(currentTimeZone);
        return this;
    }

    @JsonIgnore
    public boolean isValidCurrentTimeZone() {
        return isValidTimeZone(this.getCurrentTimeZone());
    }

    public TimeZoneDocument setCreateTimeZone(final String createTimeZone) {
        this.createTimeZone = getTimezoneFromId(createTimeZone);
        return this;
    }

    @JsonIgnore
    public boolean isValidCreateTimeZone() {
        return isValidTimeZone(this.getCreateTimeZone());
    }

    public TimeZoneDocument setServerCurrentTimeZone(final String serverCurrentTimeZone) {
        this.serverCurrentTimeZone = getTimezoneFromId(serverCurrentTimeZone);
        return this;
    }

    @JsonIgnore
    public boolean isValidServerCurrentTimeZone() {
        return isValidTimeZone(this.getServerCurrentTimeZone());
    }

    public TimeZoneDocument setServerCreteTimeZone(final String serverCreteTimeZone) {
        this.serverCreteTimeZone = getTimezoneFromId(serverCreteTimeZone);
        return this;
    }

    @JsonIgnore
    public boolean isValidServerCreteTimeZone() {
        return isValidTimeZone(this.getServerCreteTimeZone());
    }

}
