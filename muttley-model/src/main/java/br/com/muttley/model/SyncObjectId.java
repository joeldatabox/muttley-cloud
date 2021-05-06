package br.com.muttley.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = {"sync", "id"})
public class SyncObjectId {
    private String sync;
    private String id;

    public SyncObjectId(final String sync, final String id) {
        this.sync = sync;
        this.id = id;
    }
}
