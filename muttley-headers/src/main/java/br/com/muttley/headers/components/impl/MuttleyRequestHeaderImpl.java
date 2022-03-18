package br.com.muttley.headers.components.impl;

import br.com.muttley.headers.components.MuttleyRequestHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

import static br.com.muttley.headers.model.MuttleyHeader.KEY_ADMIN_SERVER;

/**
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component("requestHeader")
@RequestScope
public class MuttleyRequestHeaderImpl implements MuttleyRequestHeader {
    private final HttpServletRequest request;

    @Autowired
    public MuttleyRequestHeaderImpl(final HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public boolean isRequestFromAdminServer() {
        return this.hasKey(KEY_ADMIN_SERVER);
    }

    @Override
    public boolean hasKey(final String key) {
        return this.request.getHeader(key) != null;
    }

    @Override
    public String getByKey(final String key) {
        return this.request.getHeader(key);
    }
}
