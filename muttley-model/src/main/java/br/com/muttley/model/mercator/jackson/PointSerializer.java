package br.com.muttley.model.mercator.jackson;

import br.com.muttley.model.mercator.Point;
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
public class PointSerializer extends JsonSerializer<Point> {

    protected static void defaultPointSerializer(final Point point, final JsonGenerator gen) throws IOException {
        gen.writeStartArray();
        gen.writeNumber(point.getLongitude());
        gen.writeNumber(point.getLatitude());
        if (point.getElevation() != null) {
            gen.writeNumber(point.getElevation());
        }
        if (point.getTimeStamp() != null) {
            gen.writeObject(point.getTimeStamp());
        }
        if (point.getIndex() != null) {
            gen.writeNumber(point.getIndex());
        }
        gen.writeEndArray();
    }

    @Override
    public void serialize(final Point point, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        defaultPointSerializer(point, gen);
    }

}
