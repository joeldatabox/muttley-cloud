package br.com.muttley.jackson.service.infra.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Joel Rodrigues Moreira on 12/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ZonedDateTimeSerializer extends JsonSerializer<ZonedDateTime> {

    private final String pattern;

    public ZonedDateTimeSerializer(@Value("${br.com.muttley.jackson.date-pattern:yyyy-MM-dd'T'HH:mm:ss.SSSZ}") final String pattern) {
        this.pattern = pattern;
    }

    @Override
    public void serialize(ZonedDateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException, JsonProcessingException {
        jsonGenerator.writeString(dateTime.format(DateTimeFormatter.ofPattern(pattern)));
    }
}

