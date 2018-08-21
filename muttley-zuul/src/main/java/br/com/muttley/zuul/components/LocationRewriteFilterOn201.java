package br.com.muttley.zuul.components;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;

/**
 * Componete que adapta cabeçalho relacionado a criação de recursos.
 * Toda vez que algum recurso é criado o serviço deve retornar o código 201.
 * Para não ter problemas a respeito da localização, devemos sempre apontar no Location o Gateway do sistema.
 *
 * @author Joel Rodrigues Moreira on 30/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
public class LocationRewriteFilterOn201 extends ZuulFilter {

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext
                .getCurrentContext()
                .getResponseStatusCode() == 201;
    }

    @Override
    public Object run() {
        RequestContext
                .getCurrentContext()
                .getZuulResponseHeaders()
                .stream()
                .filter(p -> p.first().equals(HttpHeaders.LOCATION))
                .map(p -> {
                    final UriComponents gateUri = ServletUriComponentsBuilder.fromCurrentRequest().build();
                    p.setSecond(
                            UriComponentsBuilder.fromUriString(p.second())
                                    .scheme(gateUri.getScheme())
                                    .host(gateUri.getHost())
                                    .port(gateUri.getPort())
                                    .toUriString()
                    );
                    return p;
                })
                .forEach(p -> {
                });
        return null;
    }
}
