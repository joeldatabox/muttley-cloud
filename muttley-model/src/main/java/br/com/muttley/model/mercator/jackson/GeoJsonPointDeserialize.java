package br.com.muttley.model.mercator.jackson;

import br.com.muttley.model.mercator.GeoJsonPoint;
import br.com.muttley.model.mercator.Point;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class GeoJsonPointDeserialize {
    protected static GeoJsonPoint defaultGeoJsonPointDeserialize(final JsonParser parser, final JsonNode node) throws IOException, JsonProcessingException {
        final Point point = PointDeserializer.defaultPointDeserializer(parser, node);
        if (point == null) {
            return null;
        }
        return new GeoJsonPoint(point);
    }
}
