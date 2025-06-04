package br.com.muttley.model.security.jackson;

import br.com.muttley.model.parametrizacao.Parametro;
import br.com.muttley.model.security.events.ParametroResolverEvent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


@Component
public class ParametroListDeserializer extends JsonDeserializer<Collection<Parametro>> {
    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Override
    public Collection<Parametro> deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);

        if (node.isNull()) {
            return null;
        }
        final Collection<Parametro> parametros = new ArrayList<>();
        deserializerCollection(node, parametros);
        return parametros;
    }

    protected void deserializerCollection(final JsonNode node, final Collection<Parametro> parametros) {
        final Iterator<JsonNode> nodeIterator = node.iterator();

        while (nodeIterator.hasNext()) {
            final JsonNode currentNode = nodeIterator.next();
            if (!StringUtils.isEmpty(currentNode.asText())) {
                //verificando se o eventPublisher foi injetado no contexto do spring
                if (this.eventPublisher != null) {
                    final ParametroResolverEvent event = new ParametroResolverEvent(currentNode.asText());
                    //disparando para alguem ouvir esse evento
                    this.eventPublisher.publishEvent(event);
                    //retornando valor recuperado
                    parametros.add(event.isResolved() ? event.getUserResolver() : new Parametro().setId(event.getId()));
                } else {
                    //deserializando o usuário com apenas o userName mesmo
                    parametros.add(node.isNull() ? null : new Parametro().setId(node.asText()));
                }
            }
        }
    }


}
