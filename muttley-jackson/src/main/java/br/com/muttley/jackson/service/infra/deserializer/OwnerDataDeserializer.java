package br.com.muttley.jackson.service.infra.deserializer;

/*import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.OwnerDataImpl;*/

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira 12/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OwnerDataDeserializer extends JsonDeserializer</*OwnerData*/ Object> {
    @Override
    public List</*OwnerData*/ Object> deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);
        final List</*OwnerData*/ Object> owners = new ArrayList<>();
        node.forEach(it -> {
            try {
                owners.add(it.traverse(parser.getCodec()).readValueAs(new TypeReference</*OwnerData*/ Object>() {
                }));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return owners;
    }
}
