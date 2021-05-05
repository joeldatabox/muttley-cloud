package br.com.muttley.model.mercator.jackson;

import br.com.muttley.model.mercator.GeoJsonLineString;
import br.com.muttley.model.mercator.GeoJsonPolygon;
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
public class GeoJsonPolygonDeserializer {
    protected static GeoJsonPolygon defaultGeoJsonPolygon(final JsonParser parser, final JsonNode node) throws IOException, JsonProcessingException {
        return new GeoJsonPolygon(node.traverse(parser.getCodec()).readValueAs(new TypeReference<List<GeoJsonLineString>>() {
        }));
    }
}
