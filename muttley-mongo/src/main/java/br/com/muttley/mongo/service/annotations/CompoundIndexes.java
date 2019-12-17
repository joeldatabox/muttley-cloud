package br.com.muttley.mongo.service.annotations;

import org.springframework.data.mongodb.core.index.CompoundIndex;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Joel Rodrigues Moreira on 12/06/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CompoundIndexes {

    CompoundIndex[] value();

}
