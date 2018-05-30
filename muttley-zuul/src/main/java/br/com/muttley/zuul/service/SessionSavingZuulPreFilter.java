package br.com.muttley.zuul.service;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Joel Rodrigues Moreira on 30/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class SessionSavingZuulPreFilter extends ZuulFilter {

    private final String tokenHeader;
    private final String tokenHeaderJwt;

    public SessionSavingZuulPreFilter(
            @Value("${muttley.security.jwt.controller.tokenHeader:Authorization}") final String tokenHeader,
            @Value("${muttley.security.jwt.controller.tokenHeader-jwt:Authorization-jwt}") String tokenHeaderJwt
    ) {
        this.tokenHeader = tokenHeader;
        this.tokenHeaderJwt = tokenHeaderJwt;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext
                .getCurrentContext()
                .addZuulRequestHeader(
                        this.tokenHeaderJwt, (
                                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()
                        ).getRequest().getHeader(this.tokenHeader)
                );
        return null;
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
