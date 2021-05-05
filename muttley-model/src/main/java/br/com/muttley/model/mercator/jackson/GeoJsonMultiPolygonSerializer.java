package br.com.muttley.model.mercator.jackson;

import br.com.muttley.model.mercator.GeoJsonMultiPolygon;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class GeoJsonMultiPolygonSerializer extends JsonSerializer<GeoJsonMultiPolygon> {
    @Override
    public void serialize(final GeoJsonMultiPolygon geoJsonMultiPolygon, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();

        gen.writeStringField("type", geoJsonMultiPolygon.getType());
        gen.writeFieldName("coordinates");

        gen.writeObject(geoJsonMultiPolygon.getCoordinates());

        /*gen.writeStartArray();

        gen.writeob
        defaultPointSerializer(geoJsonMultiPoint.get'', gen);
        gen.writeEndArray();*/
        gen.writeEndObject();
    }
}
