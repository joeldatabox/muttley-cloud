package br.com.muttley.feign.service.service;

import br.com.muttley.feign.service.converters.MuttleyHttpMessageConverter;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 12/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyDecodersService {
    Set<? extends MuttleyHttpMessageConverter> getDecoders();
}
