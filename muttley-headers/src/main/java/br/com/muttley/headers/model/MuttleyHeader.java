package br.com.muttley.headers.model;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Joel Rodrigues Moreira on 29/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyHeader extends MuttleyRequestMetaData {
    public MuttleyHeader(final String key, final HttpServletRequest request) {
        super(key, request.getHeader(key));
    }
}
