package br.com.muttley.utils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
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
                try (ReadableByteChannel readableByteChannel = Channels.newChannel(localDownload.openStream())) {
                    try (FileChannel fileChannel = new FileOutputStream(destination.toFile()).getChannel()) {
                        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    }
                }
            } catch (final IOException exception) {
                throw new RuntimeException(exception);
            }
        }
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
                    try (Stream<Path> filesStream = Files.list(file.getParent())) {
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
}
