package br.com.muttley.metadata.anotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Joel Rodrigues Moreira on 19/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Retention(RUNTIME)
@Target({ElementType.TYPE})
public @interface Version {
    String value() default "1";

    String field() default "_version";
}
