package br.com.muttley.admin.server.service.impl;

import br.com.muttley.feign.service.service.MuttleyHeadersMetadataService;
import br.com.muttley.headers.components.MuttleyCurrentVersion;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static br.com.muttley.headers.model.MuttleyHeader.KEY_ADMIN_SERVER;

/**
 * @author Joel Rodrigues Moreira 22/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class MuttleyHeadersMetadataServiceImpl implements MuttleyHeadersMetadataService {
    private boolean resolved = false;
    private MuttleyCurrentVersion currentVersion;
    @Autowired
    protected ObjectProvider<MuttleyCurrentVersion> currentVersionProvider;

    @Override
    public Map<String, String> getHeadersMetadata() {
        final Map<String, String> headers = new HashMap<>(1);
        try {
            if (!resolved) {
                resolved = true;
                this.currentVersion = currentVersionProvider.getIfAvailable();
            }
            if (currentVersion != null) {
                headers.put(KEY_ADMIN_SERVER, currentVersion.getCurrenteFromServer());
            } else {
                headers.put(KEY_ADMIN_SERVER, null);
            }
        } catch (final BeanCreationException ex) {
            headers.put(KEY_ADMIN_SERVER, null);
        }
        return headers;
    }
}
