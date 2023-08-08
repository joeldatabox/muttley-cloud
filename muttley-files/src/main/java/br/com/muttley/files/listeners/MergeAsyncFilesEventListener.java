package br.com.muttley.files.listeners;

import br.com.muttley.files.events.MergeAsyncFilesEvent;
import br.com.muttley.files.properties.Properties;
import br.com.muttley.utils.FilesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

/**
 * @author Joel Rodrigues Moreira on 03/08/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class MergeAsyncFilesEventListener {
    private final Properties properties;

    @Autowired
    public MergeAsyncFilesEventListener(final Properties properties) {
        this.properties = properties;
    }

    @EventListener(MergeAsyncFilesEvent.class)
    public void onApplicationEvent(MergeAsyncFilesEvent event) {
        event.getSource()
                .getFilesForDownload()
                .parallelStream()
                .forEach(it -> {
                    System.out.println("Baixou sync" + it.getUrl());
                    FilesUtils.downloadFile(it.getUrl(), Paths.get(properties.getFiles(), it.getPath().toString()), it.isReplaceIfExists());
                });

        event.getSource()
                .getFilesForDelete()
                .parallelStream()
                .forEach(it -> {
                    FilesUtils.removeFile(Paths.get(this.properties.getFiles(), it.getPath().toString()), it.isDropParentIfEmpty());
                });


    }
}
