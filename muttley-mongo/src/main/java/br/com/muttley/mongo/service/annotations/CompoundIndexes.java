package br.com.muttley.mongo.service.annotations;

import org.springframework.data.mongodb.core.index.CompoundIndex;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Joel Rodrigues Moreira on 17/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface CompoundIndexes {

    CompoundIndex[] value();

}
