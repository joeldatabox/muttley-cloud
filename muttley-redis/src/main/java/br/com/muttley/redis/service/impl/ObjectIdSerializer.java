package br.com.muttley.redis.service.impl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.bson.types.ObjectId;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 27/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ObjectIdSerializer extends JsonSerializer<ObjectId> {
    @Override
    public void serialize(ObjectId value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException, JsonProcessingException {
        jsonGenerator.writeString(value == null ? null : value.toString());
    }
/*
    @Override
    public void serializeWithType(final ObjectId value, final JsonGenerator gen, final SerializerProvider serializers, final TypeSerializer typeSer) throws IOException {
        System.out.println("#chamou ObjectIdSerializer");
        super.serializeWithType(value, gen, serializers, typeSer);
    }*/
}
