package br.com.muttley.redis.service.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 06/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

    private final String pattern;

    public ZonedDateTimeDeserializer(@Value("${br.com.muttley.jackson.date-pattern:yyyy-MM-dd'T'HH:mm:ss.SSSZ}") final String pattern) {
        this.pattern = pattern;
    }

    public ZonedDateTimeDeserializer() {
        this("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }

    @Override
    public ZonedDateTime deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final JsonNode node = parser.getCodec().readTree(parser);
        if (node.isNull()) {
            return null;
        }
        return ZonedDateTime.ofInstant(getDateFrom(node.get("date")).toInstant(), ZoneOffset.of(node.get("offset").asText()));
    }

    private Date getDateFrom(final JsonNode node) {
        try {
            return new SimpleDateFormat(this.pattern).parse(node.asText());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
