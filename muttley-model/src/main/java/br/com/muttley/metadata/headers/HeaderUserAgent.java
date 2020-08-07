package br.com.muttley.metadata.headers;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpHeaders.USER_AGENT;

/**
 * @author Joel Rodrigues Moreira on 30/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

@Component
@RequestScope
public class HeaderUserAgent extends HeaderMuttley {
    private static final String MOBILE = "MOBILE";

    public HeaderUserAgent(HttpServletRequest request) {
        super(USER_AGENT, request);
    }

    public boolean isMobile() {
        return MOBILE.equalsIgnoreCase(getCurrentValue());
    }
}
