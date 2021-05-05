package br.com.muttley.model.mercator.jackson;

import br.com.muttley.model.mercator.GeoJsonPolygon;
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
public class GeoJsonPolygonSerializer extends JsonSerializer<GeoJsonPolygon> {
    @Override
    public void serialize(final GeoJsonPolygon geoJsonPolygon, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();

        gen.writeStringField("type", geoJsonPolygon.getType());
        gen.writeFieldName("coordinates");

        gen.writeObject(geoJsonPolygon.getCoordinates());


        gen.writeEndObject();
    }
}
