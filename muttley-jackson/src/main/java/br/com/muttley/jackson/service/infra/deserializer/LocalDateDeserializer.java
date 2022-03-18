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
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

import static br.com.muttley.model.util.DateUtils.DEFAULT_ISO_LOCAL_DATE;
import static br.com.muttley.model.util.DateUtils.DEFAULT_ISO_ZONED_DATE_TIME;
import static java.util.Arrays.asList;

/**
 * @author Joel Rodrigues Moreira on 17/06/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class LocalDateDeserializer extends JsonDeserializer<LocalDate> {
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    @Override
    public LocalDate deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);
        if (node.isNull()) {
            return null;
        }
        final String value = node.asText();
        try {
            if (this.isValideDate(value)) {
                return LocalDate.parse(value, DEFAULT_ISO_LOCAL_DATE);
            }
            return LocalDate.parse(value, DEFAULT_ISO_ZONED_DATE_TIME);

        } catch (DateTimeParseException e) {
            throw new MuttleyBadRequestException(null, parser.getCurrentName(), "Informe uma data válida ")
                    .addDetails("informado", value)
                    .addDetails("exemplos", asList(LocalDateTime.now().format(DEFAULT_ISO_ZONED_DATE_TIME), LocalDate.now().format(DEFAULT_ISO_LOCAL_DATE)));
        }
    }

    private boolean isValideDate(final String value) {
        return DATE_PATTERN.matcher(value).matches();
    }
}
