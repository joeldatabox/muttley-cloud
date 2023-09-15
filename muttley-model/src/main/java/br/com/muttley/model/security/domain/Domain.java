package br.com.muttley.model.security.domain;

/**
 * @author Joel Rodrigues Moreira on 16/02/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public enum Domain {
    /**
     * Qualquer registro que estiver marcado como {@link PRIVATE} somente quem criou e seus supervisores poderá visualizar
     */
    PRIVATE,
    /**
     * Qualquer registro que estiver marcado como {@link RESTRICTED} somente quem criou e participantes do grupo de trabalho e seus supervisores poderá visualizar
     */
    RESTRICTED,
    /**
     * Qualquer registro que estiver marcado como {@link PUBLIC} qualquer pessoa poderá visualizar o registro
     */
    PUBLIC;
}
