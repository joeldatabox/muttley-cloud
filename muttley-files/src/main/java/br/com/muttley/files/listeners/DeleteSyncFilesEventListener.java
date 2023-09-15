package br.com.muttley.files.listeners;

import br.com.muttley.files.events.DeleteSyncFilesEvent;
import br.com.muttley.files.properties.Properties;
import br.com.muttley.utils.FilesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Joel Rodrigues Moreira on 28/07/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class DeleteSyncFilesEventListener {
    private final Properties properties;
    private final Logger logger = LoggerFactory.getLogger(DeleteSyncFilesEventListener.class);

    @Autowired
    public DeleteSyncFilesEventListener(final Properties properties) {
        this.properties = properties;
    }

    @EventListener(classes = DeleteSyncFilesEvent.class)
    public void onApplicationEvent(DeleteSyncFilesEvent event) {
        event.getSource()
                .parallelStream()
                .forEach(it -> {
                    final Path path = Paths.get(this.properties.getFiles(), it.getPath().toString());
                    FilesUtils.removeFile(path, it.isDropParentIfEmpty());
                    logger.info("The successfully deleted file: \n\t Local file -> " + path.toAbsolutePath());
                });
    }
}
