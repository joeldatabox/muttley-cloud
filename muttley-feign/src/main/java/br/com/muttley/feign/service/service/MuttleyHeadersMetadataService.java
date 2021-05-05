package br.com.muttley.feign.service.service;

import java.util.Map;

/**
 * @author Joel Rodrigues Moreira 22/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface MuttleyHeadersMetadataService {
    Map<String, String> getHeadersMetadata();
}
