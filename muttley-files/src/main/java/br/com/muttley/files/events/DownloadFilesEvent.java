package br.com.muttley.files.events;

import br.com.muttley.files.model.FileForDownload;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 28/07/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
abstract class DownloadFilesEvent extends ApplicationEvent {

    final Set<FileForDownload> files;

    protected DownloadFilesEvent(final Set<FileForDownload> files) {
        super(files);
        this.files = files;
    }

    @Override
    public Set<FileForDownload> getSource() {
        return this.files;
    }
}
