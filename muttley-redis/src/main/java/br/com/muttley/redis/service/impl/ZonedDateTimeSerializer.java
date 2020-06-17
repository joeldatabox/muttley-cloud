package br.com.muttley.redis.service.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Value;
import sun.util.calendar.ZoneInfo;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author Joel Rodrigues Moreira on 12/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {
    private static final String TIMEZONE_REGEX = "(([+-]|)([01]?[0-9]|2[0-3]):[0-5][0-9])|(([+-]|)([01]?[0-9]|2[0-3])([0-5][0-9]))";
    private static final Pattern TIMEZONE_PATTERN = Pattern.compile(TIMEZONE_REGEX);
    private final String pattern;


    public ZonedDateTimeSerializer(@Value("${br.com.muttley.jackson.date-pattern:yyyy-MM-dd'T'HH:mm:ss.SSSZ}") final String pattern) {
        this.pattern = pattern;
    }

    public ZonedDateTimeSerializer() {
        this("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }

    @Override
    public void serialize(ZonedDateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException, JsonProcessingException {
        if (dateTime != null) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("date", dateTime.format(DateTimeFormatter.ofPattern(pattern)));
            jsonGenerator.writeStringField("offset", getTimezoneFromId(dateTime.getOffset().toString()));
            jsonGenerator.writeEndObject();
        } else {
            jsonGenerator.writeNull();
        }
    }

    private static boolean isValidTimeZone(final String timezone) {
        return timezone != null ? TIMEZONE_PATTERN.matcher(timezone).matches() : false;
    }

    private static String getTimezoneFromId(String zoneId) {
        if ("z".equalsIgnoreCase(zoneId)) {
            return "+00:00";
        }
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
