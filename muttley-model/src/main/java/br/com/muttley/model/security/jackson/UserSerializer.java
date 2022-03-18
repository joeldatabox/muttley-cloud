package br.com.muttley.model.security.jackson;

import br.com.muttley.model.security.User;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserSerializer extends JsonSerializer<User> {
    @Override
    public void serialize(final User user, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeString(user != null ? user.getUserName() : null);
    }


    /*@Override
    public void serializeWithType(final User value, final JsonGenerator gen, final SerializerProvider serializers, final TypeSerializer typeSer) throws IOException {
        //super.serializeWithType(value, gen, serializers, typeSer);
        typeSer.writeTypePrefixForScalar(this, gen, User.class);
        serialize(value, gen, serializers);
        typeSer.writeTypeSuffixForScalar(this, gen);
    }*/
}
