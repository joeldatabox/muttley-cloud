package br.com.muttley.rest.hateoas.resource.jackson;

import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.model.util.StreamUtil;
import br.com.muttley.rest.hateoas.resource.PageableResource;
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

import static java.util.stream.Collectors.toList;

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
