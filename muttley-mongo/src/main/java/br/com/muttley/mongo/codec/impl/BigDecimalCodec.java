package br.com.muttley.mongo.codec.impl;

import br.com.muttley.mongo.codec.MuttleyMongoCodec;
import br.com.muttley.mongo.converters.BigDecimalToDecimal128Converter;
import br.com.muttley.mongo.converters.Decimal128ToBigDecimalConverter;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Transformer;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.math.BigDecimal;

/**
 * @author Joel Rodrigues Moreira on 22/08/2019.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class BigDecimalCodec implements MuttleyMongoCodec<BigDecimal> {
    @Override
    public CodecProvider getCodecProvider() {
        return new CodecProvider() {
            @Override
            public <T> Codec<T> get(final Class<T> aClass, final CodecRegistry codecRegistry) {
                if (BigDecimal.class.isAssignableFrom(aClass)) {
                    return (Codec<T>) new Codec<BigDecimal>() {
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
                    };
                }
                return null;
            }
        };
    }

    @Override
    public Transformer getTransformer() {
        return new Transformer() {
            @Override
            public Object transform(final Object object) {
                return new BigDecimalToDecimal128Converter().convert((BigDecimal) object);
            }
        };
    }

    @Override
    public Class<BigDecimal> getEncoderClass() {
        return BigDecimal.class;
    }
}
