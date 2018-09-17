package br.com.muttley.model.jackson.converter;

import br.com.muttley.model.Document;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 29/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class DocumentSerializer extends JsonSerializer<Document> {
    @Override
    public void serialize(final Document document, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        if (document != null) {
            gen.writeString(document.getId().toString());
        } else {
            gen.writeNull();
        }

    }
}


