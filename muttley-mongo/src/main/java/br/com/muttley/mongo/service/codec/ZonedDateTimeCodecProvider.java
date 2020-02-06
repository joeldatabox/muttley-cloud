package br.com.muttley.mongo.service.codec;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;

import java.time.OffsetDateTime;

/**
 * @author Joel Rodrigues Moreira on 22/08/2019.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ZonedDateTimeCodecProvider implements CodecProvider {
    @Override
    public <T> Codec<T> get(final Class<T> aClass, final CodecRegistry codecRegistry) {
        if (OffsetDateTime.class.isAssignableFrom(aClass)) {
            return (Codec<T>) new ZonedDateTimeCodec();
        }
        return null;
    }
}
