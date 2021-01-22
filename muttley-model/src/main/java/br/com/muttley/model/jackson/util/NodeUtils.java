package br.com.muttley.model.jackson.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira 22/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class NodeUtils {
    public static String readAsText(final JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText();
    }

    public static String readAsText(final String fieldName, final JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return readAsText(node.get(fieldName));
    }

    public static <T> T readNodeAsType(final JsonNode node, final JsonParser parser, TypeReference<?> typeReference) throws IOException {
        if (node == null || node.isNull()) {
            return null;
        }
        return node.traverse(parser.getCodec()).readValueAs(typeReference);
    }
}
