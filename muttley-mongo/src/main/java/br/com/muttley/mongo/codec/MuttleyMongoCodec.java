package br.com.muttley.mongo.codec;

import org.bson.Transformer;
import org.bson.codecs.configuration.CodecProvider;

/**
 * @author Joel Rodrigues Moreira on 22/08/2019.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyMongoCodec<T> {
    CodecProvider getCodecProvider();

    Transformer getTransformer();

    Class<T> getEncoderClass();
}
