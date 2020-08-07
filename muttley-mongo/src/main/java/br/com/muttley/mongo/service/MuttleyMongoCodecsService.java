package br.com.muttley.mongo.service;

import br.com.muttley.mongo.codec.MuttleyMongoCodec;

/**
 * Interface de serviço para facílitar a customização de codecs do MongoDB
 *
 * @author Joel Rodrigues Moreira on 22/08/2019.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyMongoCodecsService {
    MuttleyMongoCodec[] getCustomCodecs();
}
