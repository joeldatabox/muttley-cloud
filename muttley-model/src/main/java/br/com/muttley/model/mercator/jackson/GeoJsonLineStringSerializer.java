package br.com.muttley.model.mercator.jackson;

import br.com.muttley.model.mercator.GeoJsonLineString;
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
public class GeoJsonLineStringSerializer extends JsonSerializer<GeoJsonLineString> {
    @Override
    public void serialize(final GeoJsonLineString geoJsonLineString, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();

        gen.writeStringField("type", geoJsonLineString.getType());
        gen.writeFieldName("coordinates");

        gen.writeObject(geoJsonLineString.getCoordinates());


        gen.writeEndObject();
    }
}
