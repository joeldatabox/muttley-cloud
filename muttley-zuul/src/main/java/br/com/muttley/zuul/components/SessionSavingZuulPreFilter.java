package br.com.muttley.zuul.components;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static br.com.muttley.zuul.properties.Properties.TOKEN_HEADER;
import static br.com.muttley.zuul.properties.Properties.TOKEN_HEADER_JWT;

/**
 * @author Joel Rodrigues Moreira on 30/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class SessionSavingZuulPreFilter extends ZuulFilter {
    private static final String PRE = "pre";
    @Value(TOKEN_HEADER_JWT)
    private String tokenHeaderJwt;
    @Value(TOKEN_HEADER)
    private String tokenHeader;

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext
                .getCurrentContext()
                .addZuulRequestHeader(
                        this.tokenHeaderJwt,
                        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                                .getRequest()
                                .getHeader(
                                        this.tokenHeader
                                )
                );
        return null;
    }

    @Override
    public String filterType() {
        return PRE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
