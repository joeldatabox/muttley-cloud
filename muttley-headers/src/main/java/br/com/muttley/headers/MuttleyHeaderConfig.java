package br.com.muttley.headers;

import br.com.muttley.headers.components.MuttleyCurrentTimezone;
import br.com.muttley.headers.components.MuttleyCurrentVersion;
import br.com.muttley.headers.components.MuttleyUserAgent;
import br.com.muttley.headers.components.MuttleyUserAgentName;
import br.com.muttley.headers.services.MetadataService;
import br.com.muttley.headers.services.impl.MetadataServiceImpl;
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
    public MuttleyUserAgent getUserAgent(@Autowired final HttpServletRequest request) {
        return new MuttleyUserAgent(request);
    }

    @Bean(name = "currentTimezone")
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    public MuttleyCurrentTimezone getCurrentTimezone(@Autowired final HttpServletRequest request) {
        return new MuttleyCurrentTimezone(request);
    }

    @Bean(name = "currentVersion")
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    @Autowired
    public MuttleyCurrentVersion getCurrentVersion(final HttpServletRequest request, final BuildProperties buildProperties) {
        return new MuttleyCurrentVersion(request, buildProperties);
    }

    @Bean(name = "userAgentName")
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    public MuttleyUserAgentName getUserAgentName(@Autowired final HttpServletRequest request) {
        return new MuttleyUserAgentName(request);
    }

    @Bean
    public MetadataService getMetadataService() {
        return new MetadataServiceImpl();
    }
}
