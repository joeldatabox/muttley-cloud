package br.com.muttley.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @author Joel Rodrigues Moreira on 28/07/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class FilesUtils {
    /**
     * @param localDownload   -> Url que contem o arquivo a ser baixado
     * @param destination     -> Destino para onde que deve ser baixado juntamente com o nome do arquivo
     * @param replaceIfExists -> Caso o arquivo exista o mesmo será substituido
     */
    public static void downloadFile(final URL localDownload, final Path destination, final boolean replaceIfExists) {

        //verificando se o arquivo já existe e se o mesmo deve ser substituido
        if (Files.notExists(destination) || replaceIfExists) {
            //pegan o diretório que deve ser armazenado o arquivo
            final Path directory = destination.getParent();
            try {
                //criando diretório caso ele não exista
                if (Files.notExists(directory)) {
                    Files.createDirectories(directory);
                }
                //fazendo o download do arquivo
                try (final ReadableByteChannel readableByteChannel = Channels.newChannel(localDownload.openStream())) {
                    try (final FileChannel fileChannel = new FileOutputStream(destination.toFile()).getChannel()) {
                        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    }
                }
            } catch (final IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public static Path resolveLocalFile(Path parentLocation, final URL url) {
        //pegando o caminho complento onde será armazenado o arquivo
        final Path localPath = Paths.get(parentLocation.toString(), getPathFromURL(url).toString());
        //fazendo download loca caso necessário
        downloadFile(url, localPath, false);
        return localPath;
    }

    /**
     * @param file              -> Arquivo para ser deletado
     * @param dropParentIfEmpty -> caso o diretório fique fazio o mesmo será removido
     */
    public static void removeFile(final Path file, boolean dropParentIfEmpty) {

        //removendo arquivo
        try {
            //verificando se é realmente é um arquivo
            if (!Files.isDirectory(file)) {
                Files.deleteIfExists(file);

                //deletando diretório caso necessário
                if (dropParentIfEmpty) {
                    try (final Stream<Path> filesStream = Files.list(file.getParent())) {
                        //vefirifincando se o diretório está vazio
                        if (!filesStream.findFirst().isPresent()) {
                            Files.deleteIfExists(file.getParent());
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Cria uma representação de diretório local com base na url informada
     * Exemplo  "www.mysite.com.br/teste/my-files/image.jpg" será transformado para
     * "/teste/my-files/image.jpg"
     *
     * @param url -> URL esperada
     */
    public static Path getPathFromURL(final URL url) {
        return getPathFromURL(url, "UTF-8");
    }

    /**
     * Cria uma representação de diretório local com base na url informada
     * Exemplo  "www.mysite.com.br/teste/my-files/image.jpg" será transformado para
     * "/teste/my-files/image.jpg"
     *
     * @param url      -> URL esperada
     * @param enconder -> encoder necessário para resolver a url
     */
    public static Path getPathFromURL(final URL url, final String enconder) {
        try {
            return Paths.get(URLDecoder.decode(Paths.get(url.getPath()).getFileName().toString(), enconder));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }
}

