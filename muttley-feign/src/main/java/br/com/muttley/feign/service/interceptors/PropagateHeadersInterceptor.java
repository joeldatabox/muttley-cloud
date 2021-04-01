package br.com.muttley.feign.service.interceptors;

import br.com.muttley.feign.service.service.MuttleyPropagateHeadersService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static java.util.stream.Stream.of;

/**
 * @author Joel Rodrigues Moreira on 15/08/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class PropagateHeadersInterceptor implements RequestInterceptor {

    private final MuttleyPropagateHeadersService muttleyPropagateHeadersService;

    public PropagateHeadersInterceptor(final MuttleyPropagateHeadersService muttleyPropagateHeadersService) {
        this.muttleyPropagateHeadersService = muttleyPropagateHeadersService;
    }

    @Override
    public void apply(final RequestTemplate template) {
        if (this.muttleyPropagateHeadersService != null) {
            final String[] propagateHeaders = this.muttleyPropagateHeadersService.getPropagateHeaders();
            if (propagateHeaders != null && propagateHeaders.length > 0) {
                final Map<String, String> headers = getHeadersFromCurrentRequest(propagateHeaders);
                if (headers != null) {
                    headers.forEach((final String key, final String value) -> template.header(key, value));
                }
            }
        }
    }


    private Map<String, String> getHeadersFromCurrentRequest(final String[] propagateHeaders) {
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            //recuperando a requisicao corrent
            final HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            //map para armazenar o retorno
            final Map<String, String> headers = new HashMap<>(propagateHeaders.length);
            //pegando um enum para interar o mesmo
            final Enumeration headerNames = request.getHeaderNames();
            //interando
            while (headerNames.hasMoreElements()) {
                //header atual
                final String currentHeader = headerNames.nextElement().toString();
                of(propagateHeaders)
                        .parallel()
                        .forEach(prop -> {
                            if (prop.equalsIgnoreCase(currentHeader)) {
                                headers.put(prop, request.getHeader(currentHeader));
                            }
                        });
            }
            return headers;
        }

        return null;
    }

}
