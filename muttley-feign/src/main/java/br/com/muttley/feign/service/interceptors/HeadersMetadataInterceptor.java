package br.com.muttley.feign.service.interceptors;

import br.com.muttley.feign.service.service.MuttleyHeadersMetadataService;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

import static org.springframework.util.ObjectUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira 22/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class HeadersMetadataInterceptor implements RequestInterceptor {
    private final ObjectProvider<MuttleyHeadersMetadataService> headersMetadataService;

    @Autowired
    public HeadersMetadataInterceptor(final ObjectProvider<MuttleyHeadersMetadataService> headersMetadataService) {
        this.headersMetadataService = headersMetadataService;
    }

    @Override
    public void apply(final RequestTemplate template) {
        final MuttleyHeadersMetadataService service = this.headersMetadataService.getIfAvailable();
        if (service != null) {
            final Map<String, String> headers = service.getHeadersMetadata();
            if (!isEmpty(headers)) {
                headers.forEach((final String key, final String value) -> template.header(key, value));
            }
        }
    }
}
