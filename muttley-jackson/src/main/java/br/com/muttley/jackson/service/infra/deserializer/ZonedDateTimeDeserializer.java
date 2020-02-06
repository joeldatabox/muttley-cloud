package br.com.muttley.jackson.service.infra.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Joel Rodrigues Moreira on 12/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ZonedDateTimeDeserializer extends JsonDeserializer<ZonedDateTime> {

    private final String pattern;

    public ZonedDateTimeDeserializer(@Value("${br.com.muttley.jackson.date-pattern:yyyy-MM-dd'T'HH:mm:ss.SSSZ}") final String pattern) {
        this.pattern = pattern;
    }

    @Override
    public ZonedDateTime deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final JsonNode node = parser.getCodec().readTree(parser);
        return ZonedDateTime.parse(node.asText(), DateTimeFormatter.ofPattern(pattern));
    }
}
