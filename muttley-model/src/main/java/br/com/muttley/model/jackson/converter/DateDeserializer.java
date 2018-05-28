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
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 22/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class DateDeserializer extends JsonDeserializer<Date> {
    private static final String END_WITH = "T00:00:00.000-";

    @Override
    public Date deserialize(final JsonParser parser, final DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);
        final String value = node.asText();
        try {
            if (value.contains(END_WITH)) {
                return DefaultDateFormatConfig.createDateTime().parse(value);
            }
            return Date.from(
                    LocalDateTime
                            .ofInstant(DefaultDateFormatConfig.createDateTime().parse(value).toInstant(), ZoneId.systemDefault())
                            .withHour(0)
                            .withMinute(0)
                            .withSecond(0)
                            .withNano(0)
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
            );
        } catch (ParseException e) {
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
}
