package br.com.muttley.headers;

import br.com.muttley.headers.components.MuttleyCurrentTimezone;
import br.com.muttley.headers.components.MuttleyCurrentVersion;
import br.com.muttley.headers.components.MuttleyRequestHeader;
import br.com.muttley.headers.components.MuttleyUserAgent;
import br.com.muttley.headers.components.MuttleyUserAgentName;
import br.com.muttley.headers.services.MetadataService;
import br.com.muttley.headers.services.impl.MetadataServiceImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

@Configuration
public class MuttleyHeaderConfig {
    @Bean(name = "userAgent")
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    public MuttleyUserAgent getUserAgent(@Autowired final ObjectProvider<HttpServletRequest> requestProvider) {
        return new MuttleyUserAgent(requestProvider);
    }

    @Bean(name = "currentTimezone")
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    public MuttleyCurrentTimezone getCurrentTimezone(@Autowired final ObjectProvider<HttpServletRequest> requestProvider) {
        return new MuttleyCurrentTimezone(requestProvider);
    }

    @Bean(name = "currentVersion")
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    @Autowired
    public MuttleyCurrentVersion getCurrentVersion(final ObjectProvider<HttpServletRequest> requestProvider, final BuildProperties buildProperties) {
        return new MuttleyCurrentVersion(requestProvider, buildProperties);
    }

    @Bean(name = "userAgentName")
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    public MuttleyUserAgentName getUserAgentName(@Autowired final ObjectProvider<HttpServletRequest> requestProvider) {
        return new MuttleyUserAgentName(requestProvider);
    }

    @Bean
    public MetadataService getMetadataService() {
        return new MetadataServiceImpl();
    }

    @Bean("requestHeader")
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    public MuttleyRequestHeader getRequestHeader(@Autowired final HttpServletRequest request) {
        return new MuttleyRequestHeader(request);
    }
}
