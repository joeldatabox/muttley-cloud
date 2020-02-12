package br.com.muttley.mongo.service.infra.migration;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 10/02/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyMigrationSource {
    /**
     * Versão da atualização
     */
    long getVersion();


    String getDescription();

    /**
     * .
     * Itens a ser processados
     */
    Set<MuttleyMigrationContent> getContents();


}
