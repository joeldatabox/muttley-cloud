package br.com.muttley.rest.hateoas.resource.jackson;

import br.com.muttley.rest.hateoas.resource.PageableResource;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 29/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class PageableResourceDeserialize<T> extends JsonDeserializer<PageableResource<T>> {

    @Override
    public PageableResource<T> deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        /*final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);

        List<T> list = new ArrayList<>();
        final JsonNode nodeRecords = node.get("records");
        if (nodeRecords != null && !nodeRecords.isNull() && nodeRecords.isArray()) {
            list = (List<T>) StreamUtil.of(nodeRecords.iterator())
                    .map(it -> {
                        try {
                            return it.traverse(parser.getCodec()).readValueAs(new TypeReference<T>() {
                            });
                        } catch (IOException e) {
                            throw new MuttleyException(e);
                        }
                    }).collect(toList());

        }

        return new PageableResource<T>(node.traverse(parser.getCodec()).readValuesAs(new TypeReference<List<T>>() {
        }), null);*/

        return null;
    }
}
