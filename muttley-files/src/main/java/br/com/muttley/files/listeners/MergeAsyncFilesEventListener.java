package br.com.muttley.files.listeners;

import br.com.muttley.files.events.MergeAsyncFilesEvent;
import br.com.muttley.files.properties.Properties;
import br.com.muttley.utils.FilesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Joel Rodrigues Moreira on 03/08/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class MergeAsyncFilesEventListener {
    private final Properties properties;
    private final Logger logger = LoggerFactory.getLogger(MergeAsyncFilesEventListener.class);

    @Autowired
    public MergeAsyncFilesEventListener(final Properties properties) {
        this.properties = properties;
    }

    @EventListener(MergeAsyncFilesEvent.class)
    @Async
    public void onApplicationEvent(MergeAsyncFilesEvent event) {
        //removendo todos os arquivos primeiramente
        event.getSource()
                .getFilesForDelete()
                .parallelStream()
                .forEach(it -> {
                    try {
                        final Path path = Paths.get(this.properties.getFiles(), it.getPath().toString());
                        FilesUtils.removeFile(path, it.isDropParentIfEmpty());
                        logger.info("The successfully deleted file: \n\t Local file -> " + path.toAbsolutePath());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

        //baixando os arquivo necessários
        event.getSource()
                .getFilesForDownload()
                .parallelStream()
                .forEach(it -> {
                    try {
                        final Path path = Paths.get(this.properties.getFiles(), it.getPath().toString());
                        FilesUtils.downloadFile(it.getUrl(), path, it.isReplaceIfExists());
                        logger.info("The file has been successfully downloaded: \n\t Local file -> " + path.toAbsolutePath() + "\n\t Remote file -> " + it.getUrl());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });


    }
}
