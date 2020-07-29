package br.com.muttley.mongo.migration;

/**
 * @author Joel Rodrigues Moreira on 28/07/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyMigrationContent {

    /**
     * Collections que devem ser atualizadas
     */
    String[] onCollections();

    /**
     * Filtro basico de itens a serem atualizados
     */
    String getCondictions();

    /**
     * Atualizações necessárias
     */
    String getUpdate();
}
