package br.com.muttley.files.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Joel Rodrigues Moreira on 28/07/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@EqualsAndHashCode(of = "url")
public class FileForDownload {
    final URL url;
    final Path path;
    final boolean replaceIfExists;

    public FileForDownload(final URL url, final Path path, final boolean replaceIfExists) {
        this.url = url;
        this.path = path;
        this.replaceIfExists = replaceIfExists;
    }

    public FileForDownload(final URL url, final Path path) {
        this(url, path, false);
    }

    public FileForDownload(final String url, final String path) throws MalformedURLException {
        this(toURL(url), Paths.get(path), false);
    }

    private static URL toURL(final String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public FileForDelete toFileForDelete() {
        return new FileForDelete(this.path);
    }

    public FileForDelete toFileForDelete(final boolean removeDirectory) {
        return new FileForDelete(this.path, removeDirectory);
    }
}
