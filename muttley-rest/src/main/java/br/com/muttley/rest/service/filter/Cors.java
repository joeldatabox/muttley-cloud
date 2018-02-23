package br.com.muttley.rest.service.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 29/01/18.
 * @project muttley-cloud
 */
@WebFilter(urlPatterns = "/*")
public class Cors implements Filter {
    private static final String ORIGIN = "Access-Control-Allow-Origin";
    private static final String CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String METHODS = "Access-Control-Allow-Methods";
    private static final String AGE = "Access-Control-Max-Age";
    private static final String HEADERS = "Access-Control-Allow-Headers";
    private static final String POWERED_BY = "X-Powered-By";


    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse resp, final FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) resp;

        //Camuflando a tecnologia da aplicação
        response.setHeader(POWERED_BY, "ASP.NET");
        response.setHeader(ORIGIN, request.getHeader("Origin"));
        response.setHeader(CREDENTIALS, "true");
        response.setHeader(METHODS, "POST, GET, PUT, DELETE");
        response.setHeader(AGE, "3600");
        response.setHeader(HEADERS, "Content-Type, Accept, X-Requested-With, remember-me, Authorization, Origin");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}
