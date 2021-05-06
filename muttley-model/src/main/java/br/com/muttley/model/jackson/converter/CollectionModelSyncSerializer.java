package br.com.muttley.model.jackson.converter;

import br.com.muttley.model.ModelSync;
import br.com.muttley.model.SerializeType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Joel Rodrigues Moreira on 06/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class CollectionModelSyncSerializer extends JsonSerializer<Collection<ModelSync>> {

    @Autowired
    private HttpServletRequest request;

    @Override
    public void serialize(final Collection<ModelSync> collection, final JsonGenerator gen, final SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        if (CollectionUtils.isEmpty(collection)) {
            gen.writeNull();
        } else {
            final Iterator<ModelSync> collectionIterator = collection.iterator();
            final SerializeType serializerType = SerializeType.Builder.build(this.request);
            gen.writeStartArray();

            if (serializerType.isInternal() || serializerType.isObjectId()) {
                while (collectionIterator.hasNext()) {
                    gen.writeString(collectionIterator.next().getId());
                }
            } else if (serializerType.isSync()) {
                while (collectionIterator.hasNext()) {
                    gen.writeString(collectionIterator.next().getSync());
                }
            } else if (serializerType.isObjectIdAndSync()) {
                while (collectionIterator.hasNext()) {
                    final ModelSync modelSync = collectionIterator.next();
                    gen.writeStartObject();
                    gen.writeStringField("id", modelSync.getId().toString());
                    gen.writeStringField("sync", modelSync.getSync());
                    gen.writeEndObject();
                }
            } else {
                while (collectionIterator.hasNext()) {
                    gen.writeString(collectionIterator.next().getId());
                }
            }
            gen.writeEndArray();
        }
    }
}
