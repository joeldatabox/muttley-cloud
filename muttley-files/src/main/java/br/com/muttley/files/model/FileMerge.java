package br.com.muttley.files.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 03/08/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class FileMerge {
    final Set<FileForDownload> filesForDownload;
    final Set<FileForDelete> filesForDelete;

    public FileMerge(Set<FileForDownload> filesForDownload, Set<FileForDelete> filesForDelete) {
        this.filesForDownload = filesForDownload;
        this.filesForDelete = filesForDelete;
    }

    public FileMerge(Set<FileForDownload> filesForDownload) {
        this(filesForDownload, null);
    }
}
