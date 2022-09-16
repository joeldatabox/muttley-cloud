package br.com.muttley.model.security.events;

import br.com.muttley.model.security.XAPIToken;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 15/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class XAPITokenAfterCacheLoadEvent extends ApplicationEvent {

    public XAPITokenAfterCacheLoadEvent(final String xAPIToken, final XAPIToken XAPIToken) {
        super(new APITokenAfterCacheLoadEventSource(xAPIToken, XAPIToken));
    }

    @Override
    public APITokenAfterCacheLoadEventSource getSource() {
        return (APITokenAfterCacheLoadEventSource) super.getSource();
    }

    public String getXAPIToken() {
        return this.getSource().getxAPIToken();
    }

    public XAPIToken getAPIToken() {
        return this.getSource().getXAPIToken();
    }

    private static class APITokenAfterCacheLoadEventSource {
        private final String xAPIToken;
        private final XAPIToken XAPIToken;

        private APITokenAfterCacheLoadEventSource(String xAPIToken, XAPIToken XAPIToken) {
            this.xAPIToken = xAPIToken;
            this.XAPIToken = XAPIToken;
        }

        public XAPIToken getXAPIToken() {
            return XAPIToken;
        }

        public String getxAPIToken() {
            return xAPIToken;
        }
    }
}
