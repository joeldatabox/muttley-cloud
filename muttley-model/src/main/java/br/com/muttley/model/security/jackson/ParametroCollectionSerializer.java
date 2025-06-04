package br.com.muttley.model.security.jackson;

import br.com.muttley.model.parametrizacao.Parametro;
import br.com.muttley.model.security.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collection;


public class ParametroCollectionSerializer extends JsonSerializer<Collection<Parametro>> {
    @Override
    public void serialize(final Collection<Parametro> user, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        if (user == null) {
            gen.writeNull();
        } else {
            gen.writeStartArray();
            for (final Parametro currentUser : user) {
                if (currentUser != null && !StringUtils.isEmpty(currentUser.getId())) {
                    gen.writeString(currentUser != null ? currentUser.getId() : null);
                }
            }
            gen.writeEndArray();
        }
    }
}
