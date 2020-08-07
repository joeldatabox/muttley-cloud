package br.com.muttley.headers.components;

import br.com.muttley.headers.model.MuttleyHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Joel Rodrigues Moreira on 13/05/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component("userAgentName")
@RequestScope
public class MuttleyUserAgentName extends MuttleyHeader {
    private static final String USER_AGENT_NAME = "User-Agent-Name";

    public MuttleyUserAgentName(@Autowired final HttpServletRequest request) {
        super(USER_AGENT_NAME, request);
    }
}
