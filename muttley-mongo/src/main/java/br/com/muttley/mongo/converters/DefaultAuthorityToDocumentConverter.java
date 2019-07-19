package br.com.muttley.mongo.converters;

import br.com.muttley.model.security.Authority;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Joel Rodrigues Moreira on 28/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Converter padrão para a interface Authority
 */
public class DefaultAuthorityToDocumentConverter implements Converter<Authority, Document> {
    @Override
    public Document convert(final Authority authority) {
        final Document document = new Document();
        document.put("name", authority.getRole().toString());
        document.put("description", authority.getDescription());
        return document;
    }
}
