package br.com.muttley.files.listeners;

import br.com.muttley.files.events.DeleteAsyncFilesEvent;
import br.com.muttley.files.properties.Properties;
import br.com.muttley.utils.FilesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

/**
 * @author Joel Rodrigues Moreira on 28/07/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class DeleteAsyncFilesEventListener {
    private final Properties properties;

    @Autowired
    public DeleteAsyncFilesEventListener(final Properties properties) {
        this.properties = properties;
    }

    @EventListener(DeleteAsyncFilesEvent.class)
    @Async
    public void onApplicationEvent(DeleteAsyncFilesEvent event) {
        event.getSource()
                .parallelStream()
                .forEach(it -> {
                    FilesUtils.removeFile(Paths.get(this.properties.getFiles(), it.getPath().toString()), it.isDropParentIfEmpty());
                });
    }
}
