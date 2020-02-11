package br.com.muttley.mongo.service.infra;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 10/02/20.
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
