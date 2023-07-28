package br.com.muttley.files.events;

import br.com.muttley.files.model.FileForDownload;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 28/07/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class DownloadSyncFilesEvent extends DownloadFilesEvent {
    public DownloadSyncFilesEvent(Set<FileForDownload> files) {
        super(files);
    }
}
