package br.com.muttley.headers;

import br.com.muttley.headers.components.MuttleyCurrentTimezone;
import br.com.muttley.headers.components.MuttleyCurrentVersion;
import br.com.muttley.headers.components.MuttleyRequestHeader;
import br.com.muttley.headers.components.MuttleySerializeType;
import br.com.muttley.headers.components.MuttleyUserAgent;
import br.com.muttley.headers.components.MuttleyUserAgentName;
import br.com.muttley.headers.components.impl.MuttleyCurrentTimezoneImpl;
import br.com.muttley.headers.components.impl.MuttleyCurrentVersionImpl;
import br.com.muttley.headers.components.impl.MuttleyRequestHeaderImpl;
import br.com.muttley.headers.components.impl.MuttleySerializeTypeImpl;
import br.com.muttley.headers.components.impl.MuttleyUserAgentImpl;
import br.com.muttley.headers.components.impl.MuttleyUserAgentNameImpl;
import br.com.muttley.headers.services.MetadataService;
import br.com.muttley.headers.services.impl.MetadataServiceImpl;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST;

@Configuration
public class MuttleyHeaderConfig {
    @Bean(name = "userAgent")
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    public MuttleyUserAgent getUserAgent(@Autowired final ObjectProvider<HttpServletRequest> requestProvider) {
        return new MuttleyUserAgentImpl(requestProvider);
    }

    @Bean(name = "currentTimezone")
    @Primary
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    public MuttleyCurrentTimezone getCurrentTimezone(@Autowired final ObjectProvider<HttpServletRequest> requestProvider) {
        return new MuttleyCurrentTimezoneImpl(requestProvider);
    }

    @Bean(name = "serializeType")
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    public MuttleySerializeType getSerializeType(@Autowired final HttpServletRequest requestProvider) {
        return new MuttleySerializeTypeImpl(requestProvider);
    }

    @Bean(name = "currentVersion")
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    @Autowired
    public MuttleyCurrentVersion getCurrentVersion(final ObjectProvider<HttpServletRequest> requestProvider, final BuildProperties buildProperties) {
        return new MuttleyCurrentVersionImpl(requestProvider, buildProperties);
    }

    @Bean(name = "userAgentName")
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    public MuttleyUserAgentName getUserAgentName(@Autowired final ObjectProvider<HttpServletRequest> requestProvider) {
        return new MuttleyUserAgentNameImpl(requestProvider);
    }

    @Bean
    public MetadataService getMetadataService() {
        return new MetadataServiceImpl();
    }

    @Bean("requestHeader")
    @Scope(value = SCOPE_REQUEST, proxyMode = TARGET_CLASS)
    public MuttleyRequestHeader getRequestHeader(@Autowired final HttpServletRequest request) {
        return new MuttleyRequestHeaderImpl(request);
    }
}
