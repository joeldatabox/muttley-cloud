package br.com.muttley.model.security.jackson;

import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.security.AccessPlan;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.OwnerDataImpl;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.AccessPlanResolver;
import br.com.muttley.model.security.events.OwnerResolverEvent;
import br.com.muttley.model.security.events.UserResolverEvent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;

import static br.com.muttley.model.jackson.util.NodeUtils.readAsText;
import static br.com.muttley.model.jackson.util.NodeUtils.readNodeAsType;

/**
 * @author Joel Rodrigues Moreira 13/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OwnerDataDeserializerDefault extends JsonDeserializer<OwnerData> {
    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Override
    public OwnerData deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final JsonNode node = parser.getCodec().readTree(parser);
        if (node.isObject()) {
            //se o node tiver algum desses campos quer dizer que devemos desserializar uma
            //instancia de Owner completa
            if (node.has("accessPlan") || node.has("historic") || node.has("metadata")) {
                final Owner owner = new Owner();
                owner.setId(readAsText("id", node));
                owner.setDescription(readAsText("description", node));
                owner.setName(readAsText("name", node));

                owner.setUserMaster(
                        this.readUserMaster(node.get("userMaster"), parser)
                );
                owner.setAccessPlan(
                        this.readAccessPlan(node.get("accessPlan"), parser)
                );
                owner.setHistoric(
                        this.readHistoric(node.get("historic"), parser)
                );
                owner.setMetadata(
                        this.readMetadata(node.get("metadata"), parser)
                );
                return owner;
            } else {
                return new OwnerDataImpl(
                        readAsText("id", node),
                        readAsText("name", node),
                        readAsText("description", node),
                        this.readUserMaster(node.get("userMaster"), parser)
                );
            }
        } else {
            //Vamos verificar se o deserializer está no contexto do spring e que o mesmo conseguiu injetar o eventPublisher
            if (eventPublisher != null) {
                //criando evento
                final OwnerResolverEvent event = new OwnerResolverEvent(node.asText());
                //disparando para alguem ouvir esse evento
                this.eventPublisher.publishEvent(event);
                //retornando valor recuperado
                return event.isResolved() ? event.getValueResolved() : new Owner().setId(event.getSource());
            }
            /**provavelmente o deserializer está sendo usado fora do contexto do spring
             *ou seja, devemos apenas injetar o ID e nada mais
             */
            return node.isNull() ? null : new Owner().setId(node.asText());
        }
    }

    private User readUserMaster(final JsonNode node, final JsonParser parser) throws IOException {
        if (node != null && !node.isNull()) {
            if (node.isObject()) {
                return readNodeAsType(node, parser, new TypeReference<User>() {
                });
            } else
                //Vamos verificar se o deserializer está no contexto do spring e que o mesmo conseguiu injetar o eventPublisher
                if (eventPublisher != null) {
                    //criando evento
                    final UserResolverEvent event = new UserResolverEvent(node.asText());
                    //disparando para alguem ouvir esse evento
                    this.eventPublisher.publishEvent(event);
                    //retornando valor recuperado
                    return event.isResolved() ? event.getUserResolver() : new User().setUserName(event.getUserName());
                }
        }
        return null;
    }

    private AccessPlan readAccessPlan(final JsonNode node, final JsonParser parser) throws IOException {
        if (node != null && !node.isNull()) {
            if (node.isObject()) {
                return readNodeAsType(node, parser, new TypeReference<AccessPlan>() {
                });
            } else
                //Vamos verificar se o deserializer está no contexto do spring e que o mesmo conseguiu injetar o eventPublisher
                if (eventPublisher != null) {
                    //criando evento
                    final AccessPlanResolver event = new AccessPlanResolver(node.asText());
                    //disparando para alguem ouvir esse evento
                    this.eventPublisher.publishEvent(event);
                    //retornando valor recuperado
                    return event.isResolved() ? event.getValueResolved() : new AccessPlan().setId(event.getSource());
                }
        }
        return null;
    }

    private Historic readHistoric(final JsonNode node, final JsonParser parser) throws IOException {
        return readNodeAsType(node, parser, new TypeReference<Historic>() {
        });
    }

    private MetadataDocument readMetadata(final JsonNode node, final JsonParser parser) throws IOException {
        return readNodeAsType(node, parser, new TypeReference<MetadataDocument>() {
        });
    }


}
