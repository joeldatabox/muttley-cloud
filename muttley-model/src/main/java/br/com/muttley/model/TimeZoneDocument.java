package br.com.muttley.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;
import sun.util.calendar.ZoneInfo;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author Joel Rodrigues Moreira on 30/01/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class TimeZoneDocument {
    @Transient
    @JsonIgnore
    public static final String TIMEZONE_REGEX = "(([+-]|)([01]?[0-9]|2[0-3]):[0-5][0-9])|(([+-]|)([01]?[0-9]|2[0-3])([0-5][0-9]))";

    @Transient
    @JsonIgnore
    public static final Pattern TIMEZONE_PATTERN = Pattern.compile(TIMEZONE_REGEX);

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

    public static boolean isValidTimeZone(final String timezone) {
        return timezone != null ? TIMEZONE_PATTERN.matcher(timezone).matches() : false;
    }


    /***/

    public static String getTimezoneFromId(String zoneId) {
        //verificando se zona informada é válida
        if (isValidTimeZone(zoneId)) {
            //checando se esta formatada com dois pontos
            if (!zoneId.contains("+") && !zoneId.contains("-")) {
                zoneId = "+" + zoneId;
            }
            //adicionando os dois pontos caso não tenha
            if (!zoneId.contains(":")) {
                zoneId = zoneId.substring(0, zoneId.length() - 2) + ":" + zoneId.substring(zoneId.length() - 2, zoneId.length());
            }
            //se for válida só retorna a mesma

            return zoneId;
        }

        //Se chegou até aqui quer dizer que não mandou no formato de hora
        //logo devemos recuperar pelo timezone de fato
        final TimeZone timeZone = ZoneInfo.getTimeZone(zoneId);

        //se não recuperou um timezone válido,
        //podemos parar o processo
        if (timeZone == null) {
            return null;
        }

        final long hours = TimeUnit.MILLISECONDS.toHours(timeZone.getRawOffset());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeZone.getRawOffset()) - TimeUnit.HOURS.toMinutes(hours);
        // avoid -4:-30 issue
        minutes = Math.abs(minutes);

        if (hours == 0 && minutes == 0) {
            return "+00:00";
        }

        final NumberFormat numberFormat = new DecimalFormat("00");
        final String hoursFormated = numberFormat.format(hours);

        if (hours > 0) {
            return "+" + hoursFormated + ":" + String.format("%02d", minutes);
        } else if (hours == 0 && minutes > 0) {
            return "+" + hoursFormated + ":" + String.format("%02d", minutes);
        } else if (hours < 0) {
            return hoursFormated + ":" + String.format("%02d", minutes);
        } else if (hours == 0 && minutes < 0) {
            return hoursFormated + ":" + String.format("%02d", minutes);
        }
        return null;

    }
}
