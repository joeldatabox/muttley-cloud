package br.com.muttley.security.infra.component;

import br.com.muttley.model.security.events.XAPITokenAfterCacheLoadEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author Joel Rodrigues Moreira on 15/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class XAPITokenAfterCacheLoadListener implements ApplicationListener<XAPITokenAfterCacheLoadEvent> {

    @Override
    public void onApplicationEvent(XAPITokenAfterCacheLoadEvent xapiTokenAfterCacheLoadEvent) {

    }
}
