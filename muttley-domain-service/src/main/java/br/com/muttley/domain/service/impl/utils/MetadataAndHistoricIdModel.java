package br.com.muttley.domain.service.impl.utils;

import br.com.muttley.model.Historic;
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
public class MetadataAndHistoricIdModel {
    private final String id;
    private final MetadataDocument metadata;
    private final Historic historic;

    @PersistenceConstructor
    public MetadataAndHistoricIdModel(final String id, final MetadataDocument metadata, final Historic historic) {
        this.id = id;
        this.metadata = metadata;
        this.historic = historic;
    }
}
