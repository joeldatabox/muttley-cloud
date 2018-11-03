package br.com.muttley.metadata.headers;

import br.com.muttley.metadata.RequestMetaDataMuttley;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Joel Rodrigues Moreira on 02/11/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
public class HeaderMuttley extends RequestMetaDataMuttley {
    public HeaderMuttley(final String key, final HttpServletRequest request) {
        super(key, request.getHeader(key));
    }
}
