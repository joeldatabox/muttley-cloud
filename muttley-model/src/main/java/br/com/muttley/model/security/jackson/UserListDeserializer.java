package br.com.muttley.model.security.jackson;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.UserResolverEvent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * @author Joel Rodrigues Moreira on 30/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserListDeserializer extends JsonDeserializer<Collection<User>> {
    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Override
    public Collection<User> deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);

        if (node.isNull()) {
            return null;
        }
        final Collection<User> users = new ArrayList<>();
        deserializerCollection(node, users);
        return users;
    }

    protected void deserializerCollection(final JsonNode node, final Collection<User> users) {
        final Iterator<JsonNode> nodeIterator = node.iterator();

        while (nodeIterator.hasNext()) {
            final JsonNode currentNode = nodeIterator.next();
            if (!StringUtils.isEmpty(currentNode.asText())) {
                //verificando se o eventPublisher foi injetado no contexto do spring
                if (this.eventPublisher != null) {
                    final UserResolverEvent event = new UserResolverEvent(currentNode.asText());
                    //disparando para alguem ouvir esse evento
                    this.eventPublisher.publishEvent(event);
                    //retornando valor recuperado
                    users.add(event.isResolved() ? event.getUserResolved() : new User().setUserName(event.getUserName()));
                }
                //deserializando o usuário com apenas o userName mesmo
                users.add(node.isNull() ? null : new User().setUserName(node.asText()));
            }
        }
    }


}
