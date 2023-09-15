package br.com.muttley.files.events;

import br.com.muttley.files.model.FileForDelete;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 03/08/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

abstract class DeleteFilesEvent extends ApplicationEvent {

    final Set<FileForDelete> files;

    protected DeleteFilesEvent(final Set<FileForDelete> files) {
        super(files);
        this.files = files;
    }

    @Override
    public Set<FileForDelete> getSource() {
        return this.files;
    }
}
