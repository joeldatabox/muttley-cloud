package br.com.muttley.model.security.jackson;

import br.com.muttley.model.security.UserData;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 24/12/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserDataSerializer extends JsonSerializer<UserData> {
    @Override
    public void serialize(final UserData user, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeString(user != null ? user.getUserName() : null);
    }
}