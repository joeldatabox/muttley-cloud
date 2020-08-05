package br.com.muttley.model.hermes.notification.jackson;

import br.com.muttley.model.hermes.notification.TokenOrigin;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 05/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class TokenOriginDeserializer extends JsonDeserializer<TokenOrigin> {
    @Override
    public TokenOrigin deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);
        return TokenOrigin.getTokenOrigin(node.asText());
    }
}
