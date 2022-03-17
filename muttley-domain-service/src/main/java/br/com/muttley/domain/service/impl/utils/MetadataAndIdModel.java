package br.com.muttley.domain.service.impl.utils;

import br.com.muttley.model.MetadataDocument;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.PersistenceConstructor;

/**
 * @author Joel Rodrigues Moreira on 18/06/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@EqualsAndHashCode(of = "id")
public class MetadataAndIdModel {
    private final String id;
    private final MetadataDocument metadata;

    @PersistenceConstructor
    public MetadataAndIdModel(final String id, final MetadataDocument metadata) {
        this.id = id;
        this.metadata = metadata;
    }
}
