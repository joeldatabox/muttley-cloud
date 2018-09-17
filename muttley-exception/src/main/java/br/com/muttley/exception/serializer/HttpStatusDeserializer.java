package br.com.muttley.exception.serializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 20/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class HttpStatusDeserializer extends JsonDeserializer<HttpStatus> {
    @Override
    public HttpStatus deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        JsonNode node = parser.getCodec().readTree(parser);

        return HttpStatus.valueOf((node.get("statusCode")).asInt());
        /*String itemName = node.get("itemName").asText();
        int userId = (Integer) ((IntNode) node.get("createdBy")).numberValue();

        return new Item(id, itemName, new User(userId, null));


        return HttpStatus.valueOf(
                ((JsonNode) parser.getCodec().readTree(parser)).asInt()
        );*/
    }
}
