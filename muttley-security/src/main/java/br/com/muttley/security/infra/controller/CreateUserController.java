package br.com.muttley.security.infra.controller;

import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.model.security.model.User;
import br.com.muttley.security.infra.events.UserCreatedEvent;
import br.com.muttley.security.infra.service.UserService;
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
    protected UserService service;
    protected static final String NOME = "nome";
    protected static final String EMAIL = "email";
    protected static final String PASSWD = "password";

    @Autowired
    public CreateUserController(final ApplicationEventPublisher eventPublisher, final UserService service) {
        this.eventPublisher = eventPublisher;
        this.service = service;
    }

    @RequestMapping(value = "${springboot.security.jwt.controller.createEndPoint}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void save(@RequestBody Map<String, String> payload, HttpServletResponse response) {
        if (payload.isEmpty() || payload.size() < 3 || !payload.containsKey(NOME) || !payload.containsKey(EMAIL) || !payload.containsKey(PASSWD)) {
            throw new MuttleySecurityBadRequestException(User.class, null, "Informe o nome, email e a senha")
                    .addDetails(NOME, "Nome completo")
                    .addDetails(EMAIL, "Informe um email válido")
                    .addDetails(PASSWD, "Informe uma senha válida");
        }

        if (payload.size() > 3) {
            throw new MuttleySecurityBadRequestException(User.class, null, "Por favor informe somente o nome, email e a senha")
                    .addDetails(NOME, "Nome completo")
                    .addDetails(EMAIL, "Informe um email válido")
                    .addDetails(PASSWD, "Informe uma senha válida");
        }
        final User user = new User();
        user.setNome(payload.get(NOME));
        user.setEmail(payload.get(EMAIL));
        user.setPasswd(payload.get(PASSWD));
        this.eventPublisher.publishEvent(new UserCreatedEvent(service.save(user)));
    }
}
