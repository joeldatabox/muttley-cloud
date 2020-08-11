package br.com.muttley.mongo.service;

import org.springframework.core.convert.converter.Converter;

import java.util.Collection;

/**
 * Interface de serviço para facílitar a customização de converters do MongoDB
 *
 * @author Joel Rodrigues Moreira on 17/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyConvertersService {
    public Collection<? extends Converter> getCustomConverters();
}
