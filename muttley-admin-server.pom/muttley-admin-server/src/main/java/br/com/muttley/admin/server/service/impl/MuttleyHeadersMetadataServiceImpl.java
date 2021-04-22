package br.com.muttley.admin.server.service.impl;

import br.com.muttley.feign.service.service.MuttleyHeadersMetadataService;
import br.com.muttley.headers.components.MuttleyCurrentVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira 22/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class MuttleyHeadersMetadataServiceImpl implements MuttleyHeadersMetadataService {
    private static final String KEY = "MuttleyAdminServe";
    @Autowired
    protected MuttleyCurrentVersion currentVersion;

    @Override
    public Map<String, String> getHeadersMetadata() {
        final Map<String, String> headers = new HashMap<>(1);
        headers.put(KEY, currentVersion.getCurrenteFromServer());
        return headers;
    }
}
