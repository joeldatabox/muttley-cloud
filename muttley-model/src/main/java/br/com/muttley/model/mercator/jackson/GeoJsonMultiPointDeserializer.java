package br.com.muttley.model.mercator.jackson;


import br.com.muttley.model.mercator.GeoJsonMultiPoint;
import br.com.muttley.model.mercator.Point;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class GeoJsonMultiPointDeserializer {
    protected static GeoJsonMultiPoint defaultGeoJsonMultiPointDeserializer(final JsonParser parser, final JsonNode node) throws IOException, JsonProcessingException {
        return new GeoJsonMultiPoint(node.traverse(parser.getCodec()).readValueAs(new TypeReference<List<Point>>() {
        }));
    }
}
