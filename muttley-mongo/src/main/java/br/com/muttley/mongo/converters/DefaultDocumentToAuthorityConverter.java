package br.com.muttley.mongo.converters;

import br.com.muttley.model.security.Authority;
import br.com.muttley.model.security.AuthorityImpl;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Joel Rodrigues Moreira on 28/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Converter padrão para a interface Authority
 */
public class DefaultDocumentToAuthorityConverter implements Converter<Document, Authority> {
    @Override
    public Authority convert(final Document source) {
        return new AuthorityImpl(
                source.getString("name"),
                source.getString("description")
        );
    }
}
