package br.com.muttley.mongo.service.codec;

import br.com.muttley.mongo.service.converters.BigDecimalToDecimal128Converter;
import br.com.muttley.mongo.service.converters.Decimal128ToBigDecimalConverter;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.math.BigDecimal;

/**
 * @author Joel Rodrigues Moreira on 22/08/2019.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class BigDecimalCodec implements Codec<BigDecimal> {
    @Override
    public BigDecimal decode(final BsonReader reader, final DecoderContext context) {
        return new Decimal128ToBigDecimalConverter().convert(reader.readDecimal128());
    }

    @Override
    public void encode(final BsonWriter writer, final BigDecimal value, final EncoderContext context) {
        writer.writeDecimal128(new BigDecimalToDecimal128Converter().convert(value));
    }

    @Override
    public Class<BigDecimal> getEncoderClass() {
        return BigDecimal.class;
    }
}
