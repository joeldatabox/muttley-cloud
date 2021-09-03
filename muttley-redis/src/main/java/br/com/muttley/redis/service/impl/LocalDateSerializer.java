package br.com.muttley.redis.service.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;

import static br.com.muttley.redis.service.impl.LocalDateDeserializer.DEFAULT_ISO_LOCAL_DATE;

/**
 * @author Joel Rodrigues Moreira on 03/09/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class LocalDateSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(final LocalDate date, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        if (date == null) {
            gen.writeNull();
        }

        gen.writeString(date.format(DEFAULT_ISO_LOCAL_DATE));
    }
}

