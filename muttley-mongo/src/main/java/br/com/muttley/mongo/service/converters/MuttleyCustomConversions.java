package br.com.muttley.mongo.service.converters;

import br.com.muttley.model.security.model.Authority;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Joel Rodrigues Moreira on 28/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Classe com implementações de conversores de interfaces básica do sistema
 */
public interface MuttleyCustomConversions {
    /**
     * Converte a implementação de {@link Authority} para {@link Document}
     * <p>
     * Caso prefira, você pode utilizar o converter default já implementado {@link DefaultAuthorityToDocumentConverter}
     *
     * @return {@link Converter}
     */
    Converter<Authority, Document> getAuthorityToDocumentConverter();

    /**
     * Converte o retorno do banco de {@link Document} para uma instancia de {@link Authority}
     * <p>
     * Caso prefira, você pode utilizar o converter default já implementado {@link DefaultDocumentToAuthorityConverter}
     *
     * @return {@link Converter}
     */
    Converter<Document, Authority> getDocumentToAuthorityConverter();

}
