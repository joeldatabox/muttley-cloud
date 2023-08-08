package br.com.muttley.files.events;

import br.com.muttley.files.model.FileMerge;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 03/08/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

abstract class MergeFilesEvent extends ApplicationEvent {

    final FileMerge merge;

    protected MergeFilesEvent(FileMerge merge) {
        super(merge);
        this.merge = merge;
    }

    @Override
    public FileMerge getSource() {
        return this.merge;
    }
}
