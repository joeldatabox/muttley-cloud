package br.com.muttley.model.security.jackson;

import br.com.muttley.model.parametrizacao.Parametro;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

/**
 * @author Carolina Cedro on 14/05/2025.
 * e-mail: <a href="mailto:ana.carolina@maxxsoft.com">ana.carolina@maxxsoft.com</a>
 * @project agrifocus-cloud
 */
@Component
public class ParametroSetDeserializer extends ParametroListDeserializer {

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Override
    public Collection<Parametro> deserialize(final JsonParser parser, final DeserializationContext context) throws IOException, JsonProcessingException {
        final ObjectCodec oc = parser.getCodec();
        final JsonNode node = oc.readTree(parser);

        if (node.isNull()) {
            return null;
        }
        final Collection<Parametro> parametros = new HashSet<>();
        deserializerCollection(node, parametros);
        return parametros;
    }
}

