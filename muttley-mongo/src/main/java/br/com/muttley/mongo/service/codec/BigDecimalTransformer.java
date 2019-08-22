package br.com.muttley.mongo.service.codec;

import br.com.muttley.mongo.service.converters.BigDecimalToDecimal128Converter;
import org.bson.Transformer;

import java.math.BigDecimal;

/**
 * @author Joel Rodrigues Moreira on 22/08/2019.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class BigDecimalTransformer implements Transformer {
    @Override
    public Object transform(final Object object) {
        return new BigDecimalToDecimal128Converter().convert((BigDecimal) object);
    }
}
