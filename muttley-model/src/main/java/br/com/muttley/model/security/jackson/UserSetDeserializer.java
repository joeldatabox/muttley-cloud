package br.com.muttley.model.security.jackson;

import br.com.muttley.model.security.User;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Joel Rodrigues Moreira on 30/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserSetDeserializer extends UserListDeserializer {
    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Override
    public Collection<User> deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);

        if (node.isNull()) {
            return null;
        }
        final Collection<User> users = new HashSet<>();
        deserializerCollection(node, users);
        return users;
    }


}
