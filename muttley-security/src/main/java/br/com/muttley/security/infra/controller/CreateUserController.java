package br.com.muttley.security.infra.controller;

import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserPayLoad;
import br.com.muttley.model.security.events.UserCreatedEvent;
import br.com.muttley.security.feign.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
public class CreateUserController {

    protected final ApplicationEventPublisher eventPublisher;
    protected UserServiceClient service;
    protected static final String NOME = "name";
    protected static final String USER_NAME = "userName";
    protected static final String PASSWD = "password";

    @Autowired
    public CreateUserController(final ApplicationEventPublisher eventPublisher, final UserServiceClient service) {
        this.eventPublisher = eventPublisher;
        this.service = service;
    }

    @RequestMapping(value = "${muttley.security.jwt.controller.createEndPoint}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody Map<String, String> payload, HttpServletResponse response) {
        if (payload.isEmpty() || payload.size() < 3 || !payload.containsKey(NOME) || !payload.containsKey(USER_NAME) || !payload.containsKey(PASSWD)) {
            throw new MuttleySecurityBadRequestException(User.class, null, "Informe o nome, userName e a senha")
                    .addDetails(NOME, "Nome completo")
                    .addDetails(USER_NAME, "Informe um userName válido")
                    .addDetails(PASSWD, "Informe uma senha válida");
        }

        if (payload.size() > 3) {
            throw new MuttleySecurityBadRequestException(User.class, null, "Por favor informe somente o nome, userName e a senha")
                    .addDetails(NOME, "Nome completo")
                    .addDetails(USER_NAME, "Informe um userName válido")
                    .addDetails(PASSWD, "Informe uma senha válida");
        }
        final UserPayLoad user = new UserPayLoad(payload.get(NOME), payload.get(USER_NAME), payload.get(PASSWD));
        this.eventPublisher.publishEvent(new UserCreatedEvent(service.save(user, "true")));
    }
}
