package br.com.muttley.model.jackson.converter;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.model.jackson.DefaultDateFormatConfig;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 09/08/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    public static final DateTimeFormatter dateTimeFormatterDefault = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private static final DateTimeFormatter dateTimeFormatterOp1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");
    private static final DateTimeFormatter dateTimeFormatterOp2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    private static final DateTimeFormatter dateTimeFormatterOp3 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    public LocalDateTime deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);
        final String value = node.asText();
        try {
            switch (value.length()) {
                case 28:
                    return ZonedDateTime.parse(value, dateTimeFormatterOp3).toLocalDateTime();
                case 21:
                    return LocalDateTime.parse(value, dateTimeFormatterOp2);
                case 22:
                    return LocalDateTime.parse(value, dateTimeFormatterOp1);
                case 23:
                    return LocalDateTime.parse(value, dateTimeFormatterOp1);
                default:
                    return this.throwsExcpetion(parser);
            }
        } catch (RuntimeException e) {
            return this.throwsExcpetion(parser);

        }
    }

    private LocalDateTime throwsExcpetion(final JsonParser parser) throws IOException {
        throw new MuttleyBadRequestException(null, parser.getCurrentName(), "Informe uma data válida ")
                .addDetails("exemplo",
                        DefaultDateFormatConfig.createDateOnly().format(
                                Date.from(
                                        LocalDateTime
                                                .ofInstant(new Date().toInstant(), ZoneId.systemDefault())
                                                .withHour(0)
                                                .withMinute(0)
                                                .withSecond(0)
                                                .withNano(0)
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant()
                                )
                        ));
    }
}
