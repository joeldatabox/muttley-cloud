package br.com.muttley.files.events;

import br.com.muttley.files.model.FileMerge;

/**
 * @author Joel Rodrigues Moreira on 03/08/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MergeSyncFilesEvent extends MergeFilesEvent {
    protected MergeSyncFilesEvent(FileMerge merge) {
        super(merge);
    }
}