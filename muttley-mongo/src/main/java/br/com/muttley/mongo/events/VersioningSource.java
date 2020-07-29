package br.com.muttley.mongo.events;
import java.util.Set;
/**
 * @author Joel Rodrigues Moreira on 28/07/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface VersioningSource {
    /**
     * Versão da atualização
     */
    long getVersion();


    String getDescription();

    /**
     * .
     * Itens a ser processados
     */
    Set<VersoningContent> getContents();


}

