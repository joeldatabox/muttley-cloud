package br.com.muttley.mongo.service.codec;

import br.com.muttley.mongo.service.converters.ZonedDateTimeToBsonDocumentConverter;
import org.bson.Transformer;

import java.time.ZonedDateTime;

/**
 * @author Joel Rodrigues Moreira on 22/08/2019.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ZonedDateTimeTransformer implements Transformer {

    @Override
    public Object transform(final Object zonedDateTime) {
        return new ZonedDateTimeToBsonDocumentConverter().convert((ZonedDateTime) zonedDateTime);
    }

}
