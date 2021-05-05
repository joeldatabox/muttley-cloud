package br.com.muttley.admin.server.service.impl;

import br.com.muttley.domain.service.Validator;
import br.com.muttley.exception.throwables.MuttleyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Joel Rodrigues Moreira 23/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class AbstractNoSecurityService {
    @Autowired
    protected Validator validator;

    /**
     * O método verifica se estamos executanto dentro de um contexto de requisição HTTP
     * <p>
     * Caso estejamos em uma requisição, devemos lançar uma exception
     */
    protected void validateContext() {
        try {
            //se esssa linha for executada sem lançar uma exception, é sinal que o serviço está trabalhando sobre um contexto http
            final HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            //lançando a exception necessária
            throw new MuttleyException("ATENÇÃO, ESSE SERVIÇO NÃO PODE SER USADO EM CONTEXTO DE REQUISIÇÃO!");
        } catch (IllegalStateException ex) {
            //se chegou aqui está tudo ok podemos proceguir
        }
    }
}
