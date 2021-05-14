package br.com.muttley.rest.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;

import static br.com.muttley.model.SerializeType.KEY_INTERNAL_FROM_HEADER;

/**
 * @author Joel Rodrigues Moreira on 11/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class InternalSerializeInterceptor implements RequestInterceptor {
    @Override
    public void apply(final RequestTemplate template) {
        template.header(KEY_INTERNAL_FROM_HEADER, "true");
    }
}
