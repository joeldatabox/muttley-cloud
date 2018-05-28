package br.com.muttley.model.jackson.converter;

import br.com.muttley.model.jackson.DefaultDateFormatConfig;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 22/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class DateSerializer extends JsonSerializer<Date> {
    @Override
    public void serialize(final Date date, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        jsonGenerator.writeString(date != null ?
                DefaultDateFormatConfig.createDateOnly().format(Date.from(
                        LocalDateTime
                                .ofInstant(date.toInstant(), ZoneId.systemDefault())
                                .withHour(0)
                                .withMinute(0)
                                .withSecond(0)
                                .withNano(0)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                )) :
                null
        );
    }
}
