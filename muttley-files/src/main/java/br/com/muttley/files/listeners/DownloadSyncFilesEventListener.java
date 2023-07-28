package br.com.muttley.files.listeners;

import br.com.muttley.files.events.DownloadSyncFilesEvent;
import br.com.muttley.files.properties.Properties;
import br.com.muttley.utils.FilesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

/**
 * @author Joel Rodrigues Moreira on 28/07/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class DownloadSyncFilesEventListener implements ApplicationListener<DownloadSyncFilesEvent> {
    private final Properties properties;

    @Autowired
    public DownloadSyncFilesEventListener(final Properties properties) {
        this.properties = properties;
    }

    @Override
    public void onApplicationEvent(DownloadSyncFilesEvent event) {
        event.getSource()
                .parallelStream()
                .forEach(it -> {
                    System.out.println("Baixou sync" + it.getUrl());
                    FilesUtils.downloadFile(it.getUrl(), Paths.get(properties.getFiles(), it.getPath().toString()), it.isReplaceIfExists());
                });
    }
}
