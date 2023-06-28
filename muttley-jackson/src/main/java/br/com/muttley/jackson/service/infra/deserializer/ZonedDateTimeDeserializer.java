package br.com.muttley.jackson.service.infra.deserializer;

import br.com.muttley.utils.DateUtils;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.sql.SQLOutput;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

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
        try {
            return ZonedDateTime.parse(node.asText(), DateTimeFormatter.ofPattern(pattern));
        } catch (DateTimeParseException exception) {
            System.out.println("COBRA O DERCI PRA ARRUMAR SAPORRAKKKKK");
            System.out.println("A DATA FORNECIDA ESTA COM PADRÕES FORA DOS CONFORMES");
            return DateUtils.toZonedDateTime(new Date(Long.valueOf(node.asText())));
        }
    }
}
