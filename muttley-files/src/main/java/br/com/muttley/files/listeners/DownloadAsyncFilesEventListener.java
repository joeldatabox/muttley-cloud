package br.com.muttley.files.listeners;

import br.com.muttley.files.events.DownloadAsyncFilesEvent;
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
public class DownloadAsyncFilesEventListener {
    private final Properties properties;

    @Autowired
    public DownloadAsyncFilesEventListener(final Properties properties) {
        this.properties = properties;
    }

    @EventListener(classes = DownloadAsyncFilesEvent.class)
    @Async
    public void onApplicationEvent(DownloadAsyncFilesEvent event) {
        event.getSource()
                .parallelStream()
                .forEach(it -> {
                    System.out.println("Baixou async" + it.getUrl());
                    FilesUtils.downloadFile(it.getUrl(), Paths.get(properties.getFiles(), it.getPath().toString()), it.isReplaceIfExists());
                });
    }
}
