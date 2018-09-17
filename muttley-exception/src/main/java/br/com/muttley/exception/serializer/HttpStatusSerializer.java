package br.com.muttley.exception.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 07/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class HttpStatusSerializer extends JsonSerializer<HttpStatus> {
    @Override
    public void serialize(final HttpStatus status, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        gen.writeStringField("statusName", status.name());
        gen.writeNumberField("statusCode", status.value());
        gen.writeStringField("reasonPhrase", status.getReasonPhrase());
        gen.writeEndObject();
    }
}
