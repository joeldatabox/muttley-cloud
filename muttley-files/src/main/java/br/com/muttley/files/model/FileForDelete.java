package br.com.muttley.files.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.nio.file.Path;

/**
 * @author Joel Rodrigues Moreira on 03/08/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class FileForDelete {
    final Path path;
    final boolean dropParentIfEmpty;

    public FileForDelete(Path path, boolean dropParentIfEmpty) {
        this.path = path;
        this.dropParentIfEmpty = dropParentIfEmpty;
    }

    public FileForDelete(Path path) {
        this.path = path;
        this.dropParentIfEmpty = false;
    }
}
