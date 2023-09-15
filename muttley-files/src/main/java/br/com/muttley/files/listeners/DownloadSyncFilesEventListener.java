package br.com.muttley.files.listeners;

import br.com.muttley.files.events.DownloadSyncFilesEvent;
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
public class DownloadSyncFilesEventListener {
    private final Properties properties;
    private final Logger logger = LoggerFactory.getLogger(DownloadSyncFilesEventListener.class);

    @Autowired
    public DownloadSyncFilesEventListener(final Properties properties) {
        this.properties = properties;
    }

    @EventListener(DownloadSyncFilesEvent.class)
    public void onApplicationEvent(DownloadSyncFilesEvent event) {
        event.getSource()
                .parallelStream()
                .forEach(it -> {
                    final Path path = Paths.get(this.properties.getFiles(), it.getPath().toString());
                    FilesUtils.downloadFile(it.getUrl(), path, it.isReplaceIfExists());
                    logger.info("The file has been successfully downloaded: \n\t Local file -> " + path.toAbsolutePath() + "\n\t Remote file -> " + it.getUrl());
                });
    }
}
