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
import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 22/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class DateTimeDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(final JsonParser parser, final DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);
        final String value = node.asText();
        try {
            return DefaultDateFormatConfig.createDateTime().parse(value);
        } catch (ParseException e) {
            throw new MuttleyBadRequestException(null, parser.getCurrentName(), "Informe uma data válida ")
                    .addDetails("exemplo", DefaultDateFormatConfig.createDateTime().format(new Date()));
        }
    }
}
