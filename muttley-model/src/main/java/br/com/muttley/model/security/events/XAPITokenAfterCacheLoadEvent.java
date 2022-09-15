package br.com.muttley.model.security.events;

import br.com.muttley.model.security.APIToken;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Joel Rodrigues Moreira on 15/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class XAPITokenAfterCacheLoadEvent extends ApplicationEvent {

    public XAPITokenAfterCacheLoadEvent(final String xAPIToken, final APIToken apiToken) {
        super(new APITokenAfterCacheLoadEventSource(xAPIToken, apiToken));
    }

    @Override
    public APITokenAfterCacheLoadEventSource getSource() {
        return (APITokenAfterCacheLoadEventSource) super.getSource();
    }

    public String getXAPIToken() {
        return this.getSource().getXAPIToken();
    }

    public APIToken getAPIToken() {
        return this.getSource().getApiToken();
    }

    @Getter
    private static class APITokenAfterCacheLoadEventSource {
        private final String xAPIToken;
        private final APIToken apiToken;

        private APITokenAfterCacheLoadEventSource(String xAPIToken, APIToken apiToken) {
            this.xAPIToken = xAPIToken;
            this.apiToken = apiToken;
        }
    }
}
