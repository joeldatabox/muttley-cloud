package br.com.muttley.model.mercator.jackson;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.model.mercator.GeoJsonPoint;
import br.com.muttley.model.mercator.TypeGeoJson;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.mongodb.core.geo.GeoJson;

import java.io.IOException;

import static br.com.muttley.model.mercator.jackson.GeoJsonLineStringDeserializer.defaultGeoJsonLineString;
import static br.com.muttley.model.mercator.jackson.GeoJsonMultiLineStringDeserializer.defaultGeoJonMultiLineStringDeserializer;
import static br.com.muttley.model.mercator.jackson.GeoJsonMultiPointDeserializer.defaultGeoJsonMultiPointDeserializer;
import static br.com.muttley.model.mercator.jackson.GeoJsonMultiPolygonDeserializer.defaulGeoJsonMultiPolygonDeserializer;
import static br.com.muttley.model.mercator.jackson.GeoJsonPointDeserialize.defaultGeoJsonPointDeserialize;
import static br.com.muttley.model.mercator.jackson.GeoJsonPolygonDeserializer.defaultGeoJsonPolygon;
import static br.com.muttley.model.mercator.jackson.PointDeserializer.defaultPointDeserializer;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class GeoJsonDeserialize extends JsonDeserializer {
    @Override
    public Object deserialize(final JsonParser parser, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);
        final JsonNode typeNode = node.get("type");
        if (typeNode == null || typeNode.isNull()) {
            if (node.isArray()) {
                return new GeoJsonPoint(defaultPointDeserializer(parser, node));
            }
            throw new MuttleyBadRequestException(GeoJson.class, "type", "Informe um type válido");
        }

        final TypeGeoJson type = TypeGeoJson.of(typeNode.asText());

        switch (type) {
            case POINT:
                return defaultGeoJsonPointDeserialize(parser, node.get("coordinates"));
            case MULTIPOINT:
                return defaultGeoJsonMultiPointDeserializer(parser, node.get("coordinates"));
            case LINESTRING:
                return defaultGeoJsonLineString(parser, node.get("coordinates"));
            case MULTILINESTRING:
                return defaultGeoJonMultiLineStringDeserializer(parser, node.get("coordinates"));
            case POLYGON:
                return defaultGeoJsonPolygon(parser, node.get("coordinates"));
            case MULTIPOLYGON:
                return defaulGeoJsonMultiPolygonDeserializer(parser, node.get("coordinates"));
        }
        return null;
    }
}
