package br.com.muttley.model.mercator.jackson;

import br.com.muttley.model.mercator.GeoJsonPoint;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

import static br.com.muttley.model.mercator.jackson.PointSerializer.defaultPointSerializer;


/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class GeoJsonPointSerializer extends JsonSerializer<GeoJsonPoint> {
    @Override
    public void serialize(final GeoJsonPoint geoJsonPoint, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();

        gen.writeStringField("type", geoJsonPoint.getType());
        gen.writeFieldName("coordinates");
        defaultPointSerializer(geoJsonPoint, gen);

        gen.writeEndObject();
    }
}
