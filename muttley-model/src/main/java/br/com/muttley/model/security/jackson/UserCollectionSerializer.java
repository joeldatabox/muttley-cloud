package br.com.muttley.model.security.jackson;

import br.com.muttley.model.security.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Joel Rodrigues Moreira on 30/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserCollectionSerializer extends JsonSerializer<Collection<User>> {
    @Override
    public void serialize(final Collection<User> user, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        if (user == null) {
            gen.writeNull();
        } else {
            gen.writeStartArray();
            for (final User currentUser : user) {
                if (currentUser != null && !StringUtils.isEmpty(currentUser.getEmail())) {
                    gen.writeString(currentUser != null ? currentUser.getEmail() : null);
                }
            }
            gen.writeEndArray();
        }
    }
}
