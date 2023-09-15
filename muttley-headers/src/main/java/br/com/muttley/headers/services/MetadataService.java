package br.com.muttley.headers.services;

import br.com.muttley.model.Document;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.domain.Domain;

import java.util.Collection;

/**
 * @author Joel Rodrigues Moreira 12/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MetadataService {
    void generateNewMetadataFor(final User user, final Document value);

    void generateNewMetadataFor(final User user, final Document value, final Domain domain);

    void generateNewMetadataFor(final User user, final Collection<? extends Document> values);

    void generateNewMetadataFor(final User user, final Collection<? extends Document> values, final Domain domain);

    void generateMetaDataUpdateFor(final User user, final MetadataDocument currentMetadata, final Document value);
}
