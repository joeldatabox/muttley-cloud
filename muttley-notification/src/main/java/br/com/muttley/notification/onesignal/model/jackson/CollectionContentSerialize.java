package br.com.muttley.notification.onesignal.model.jackson;

import br.com.muttley.notification.onesignal.model.Content;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collection;

public class CollectionContentSerialize extends JsonSerializer<Collection<Content>> {
    @Override
    public void serialize(final Collection<Content> contents, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        if (!CollectionUtils.isEmpty(contents)) {
            for (Content content : contents) {
                gen.writeStringField(content.getLanguage().getOneSignalValue(), content.getContent());
            }
        }
        gen.writeEndObject();
    }
}
