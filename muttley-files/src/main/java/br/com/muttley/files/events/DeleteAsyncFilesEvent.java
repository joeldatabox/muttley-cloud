package br.com.muttley.files.events;

import br.com.muttley.files.model.FileForDelete;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 03/08/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class DeleteAsyncFilesEvent extends DeleteFilesEvent {
    public DeleteAsyncFilesEvent(Set<FileForDelete> files) {
        super(files);
    }
}
