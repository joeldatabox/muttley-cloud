package br.com.muttley.model.jackson.converter;

import br.com.muttley.model.ModelSync;
import br.com.muttley.model.SerializeType;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static br.com.muttley.model.SerializeType.Builder.build;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ModelSyncSerializer<T extends ModelSync> extends JsonSerializer<T> {
    //se no cabeçalho da requição tiver esse informação como true
    //devemos então serializar somente o sync
    /*private static final String SERIALIZER_TYPE = "SerializeType";
    //valor epserado para configuração do SERIALIZER_INFO
    private static final String SYNC_TYPE = "sync";
    private static final String OBJECT_ID_TYPE = "ObjectId";
    private static final String OBJECT_ID_AND_SYNC_TYPE = "ObjectIdAndSync";*/

    @Autowired
    protected HttpServletRequest request;

    @Override
    public void serialize(final T value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        if (value == null) {
            gen.writeNull();
        } else {
            final SerializeType serializerType = build(this.request);
            if (serializerType.isInternal()) {
                gen.writeString(value.getId());
            } else if (serializerType.isSync()) {
                gen.writeString(value.getSync());
            } else if (serializerType.isObjectId()) {
                gen.writeString(value.getId());
            } else if (serializerType.isObjectIdAndSync()) {
                gen.writeStartObject();
                gen.writeStringField("id", value.getId());
                gen.writeStringField("sync", value.getSync());
                gen.writeEndObject();
            } else {
                gen.writeString(value.getId());
            }

        }
    }
}