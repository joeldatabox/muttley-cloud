package br.com.muttley.zuul.components;

import br.com.muttley.zuul.property.MuttleySecurityProperty;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private MuttleySecurityProperty property;

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext
                .getCurrentContext()
                .addZuulRequestHeader(
                        this.property.getJwt().getController().getTokenHeaderJwt(),
                        ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                                .getRequest()
                                .getHeader(
                                        this.property.getJwt().getController().getTokenHeader()
                                )
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
