package br.com.muttley.headers.components;

import br.com.muttley.headers.model.MuttleyHeader;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.http.HttpHeaders.USER_AGENT;

/**
 * @author Joel Rodrigues Moreira on 29/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component("userAgente")
@RequestScope
public class MuttleyUserAgent extends MuttleyHeader {
    private static final String MOBILE = "MOBILE";

    public MuttleyUserAgent(@Autowired final ObjectProvider<HttpServletRequest> requestProvider) {
        super(USER_AGENT, requestProvider);
    }

    public boolean isMobile() {
        return MOBILE.equalsIgnoreCase(getCurrentValue());
    }
}
