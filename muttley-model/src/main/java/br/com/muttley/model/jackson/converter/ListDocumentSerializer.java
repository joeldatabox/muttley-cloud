package br.com.muttley.model.jackson.converter;

import br.com.muttley.model.Document;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Joel Rodrigues Moreira on 16/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ListDocumentSerializer extends JsonSerializer<Collection<Document>> {
    @Override
    public void serialize(final Collection<Document> documents, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        if (documents != null) {
            gen.writeStartArray();
            for (final Document doc : documents) {
                gen.writeString(doc.getId());
            }
            gen.writeEndArray();
        } else {
            gen.writeNull();
        }

    }
}
