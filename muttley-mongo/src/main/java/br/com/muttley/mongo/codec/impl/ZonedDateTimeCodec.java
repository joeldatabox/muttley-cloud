package br.com.muttley.mongo.codec.impl;

import br.com.muttley.mongo.codec.MuttleyMongoCodec;
import br.com.muttley.mongo.converters.BsonDocumentToZonedDateTimeConverter;
import br.com.muttley.mongo.converters.ZonedDateTimeToBsonDocumentConverter;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.Transformer;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.time.ZonedDateTime;

/**
 * @author Joel Rodrigues Moreira on 22/08/2019.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ZonedDateTimeCodec implements MuttleyMongoCodec<ZonedDateTime> {
    @Override
    public CodecProvider getCodecProvider() {
        return new CodecProvider() {
            @Override
            public <T> Codec<T> get(final Class<T> aClass, final CodecRegistry codecRegistry) {
                if (ZonedDateTime.class.isAssignableFrom(aClass)) {
                    return (Codec<T>) new Codec<ZonedDateTime>() {
                        @Override
                        public ZonedDateTime decode(final BsonReader reader, final DecoderContext context) {
                            return new BsonDocumentToZonedDateTimeConverter().convert(reader);
                        }

                        @Override
                        public void encode(final BsonWriter writer, final ZonedDateTime value, final EncoderContext context) {
                            new ZonedDateTimeToBsonDocumentConverter().convert(writer, value);
                        }

                        @Override
                        public Class<ZonedDateTime> getEncoderClass() {
                            return ZonedDateTime.class;
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
                return new ZonedDateTimeToBsonDocumentConverter().convert((ZonedDateTime) object);
            }
        };
    }

    @Override
    public Class<ZonedDateTime> getEncoderClass() {
        return ZonedDateTime.class;
    }
}
