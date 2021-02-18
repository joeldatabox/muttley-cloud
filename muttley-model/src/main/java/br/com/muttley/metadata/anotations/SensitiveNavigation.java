package br.com.muttley.metadata.anotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Joel Rodrigues Moreira on 11/01/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Toda propriedade que for marcada com essa anotação não poderá ter lookup para navegação gerado automaticamente
 * Isso se faz necessário para não haver a quebra do multitenancy usando o Owner
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface SensitiveNavigation {
}
