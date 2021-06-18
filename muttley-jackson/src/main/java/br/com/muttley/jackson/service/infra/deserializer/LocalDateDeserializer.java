package br.com.muttley.jackson.service.infra.deserializer;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import static br.com.muttley.model.util.DateUtils.DEFAULT_ISO_LOCAL_DATE;
import static br.com.muttley.model.util.DateUtils.DEFAULT_ISO_ZONED_DATE_TIME;

/**
 * @author Joel Rodrigues Moreira on 17/06/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    private static final String END_WITH = "T00:00:00.000-";

    @Override
    public LocalDate deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);
        if (node.isNull()) {
            return null;
        }
        final String value = node.asText();
        try {
            if (value.contains(END_WITH)) {
                return LocalDate.parse(value, DEFAULT_ISO_ZONED_DATE_TIME);
            }
            return LocalDate.parse(value, DEFAULT_ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new MuttleyBadRequestException(null, parser.getCurrentName(), "Informe uma data válida ")
                    .addDetails("informado", value)
                    .addDetails("exemplo", LocalDate.now().format(DEFAULT_ISO_LOCAL_DATE));
        }
    }
}
