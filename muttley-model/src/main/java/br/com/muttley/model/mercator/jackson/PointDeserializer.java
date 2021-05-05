package br.com.muttley.model.mercator.jackson;

import br.com.muttley.model.mercator.Point;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class PointDeserializer extends JsonDeserializer<Point> {

    protected static Point defaultPointDeserializer(final JsonParser parser, final JsonNode node) throws IOException, JsonProcessingException {

        if (node.isNull() || node.size() == 0) {
            return null;
        }

        final JsonNode jsonLongitude = node.get(0);
        final JsonNode jsonLatitude = node.get(1);

        final Point point = new Point(jsonLongitude != null ? jsonLongitude.asDouble() : null, jsonLatitude != null ? jsonLatitude.asDouble() : null);
        if (node.size() == 3) {
            if (node.get(2).isNumber()) {
                point.setElevation(node.get(2).asDouble());
            } else {
                point.setTimeStamp(node.get(2).traverse(parser.getCodec()).readValueAs(ZonedDateTime.class));
            }
        } else if (node.size() == 4) {
            point.setElevation(node.get(2).asDouble());
            point.setTimeStamp(node.get(3).traverse(parser.getCodec()).readValueAs(ZonedDateTime.class));
        }
        return point;
    }

    @Override
    public Point deserialize(final JsonParser parser, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final ObjectCodec objectCodec = parser.getCodec();

        final JsonNode node = objectCodec.readTree(parser);

        return defaultPointDeserializer(parser, node);
    }
}
