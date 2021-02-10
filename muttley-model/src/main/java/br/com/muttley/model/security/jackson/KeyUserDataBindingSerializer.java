package br.com.muttley.model.security.jackson;

import br.com.muttley.model.security.KeyUserDataBinding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira 10/02/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class KeyUserDataBindingSerializer extends JsonSerializer<KeyUserDataBinding> {

    @Override
    public void serialize(final KeyUserDataBinding key, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        if (key == null) {
            gen.writeNull();
        }
        gen.writeString(key.getKey());
    }
}
