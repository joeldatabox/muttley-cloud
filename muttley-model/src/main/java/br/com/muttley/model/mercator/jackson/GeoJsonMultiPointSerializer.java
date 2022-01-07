package br.com.muttley.model.mercator.jackson;

import br.com.muttley.model.mercator.GeoJsonMultiPoint;
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
public class GeoJsonMultiPointSerializer extends JsonSerializer<GeoJsonMultiPoint> {
    @Override
    public void serialize(final GeoJsonMultiPoint geoJsonMultiPoint, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();

        gen.writeStringField("type", geoJsonMultiPoint.getType());
        gen.writeFieldName("coordinates");

        gen.writeObject(geoJsonMultiPoint.getCoordinates());

        /*gen.writeStartArray();

        gen.writeob
        defaultPointSerializer(geoJsonMultiPoint.get'', gen);
        gen.writeEndArray();*/
        gen.writeEndObject();
    }
}