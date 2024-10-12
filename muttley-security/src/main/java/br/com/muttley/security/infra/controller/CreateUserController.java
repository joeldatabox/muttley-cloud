package br.com.muttley.security.infra.controller;

import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserPayLoad;
import br.com.muttley.model.security.events.UserCreatedEvent;
import br.com.muttley.model.security.preference.Foto;
import br.com.muttley.security.feign.UserServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
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
    protected static final String DECRIPTION = "description";
    protected static final String USER_NAME = "userName";
    protected static final String FOTO = "foto";
    protected static final String EMAIL = "email";
    protected static final String EMAIL_SECUNDARIO = "emailSecundario";
    protected static final String PASSWD = "password";
    protected static final String NICK_NAMES = "nickNames";
    protected static final String FONE = "fone";
    protected static final String CODE_VERIFICATION = "code";

    protected static final String RENEW_CODE_VERIFICATION = "renewCode";

    @Autowired
    public CreateUserController(final ApplicationEventPublisher eventPublisher, final UserServiceClient service) {
        this.eventPublisher = eventPublisher;
        this.service = service;
    }

    @RequestMapping(value = "${muttley.security.jwt.controller.createEndPoint}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity save(@RequestBody Map<String, Object> payload, HttpServletResponse response) {

        if (
                !((payload.containsKey("name") && payload.containsKey("password") && payload.containsKey("email")) ||
                        (payload.containsKey("name") && payload.containsKey("password") && payload.containsKey("userName")) ||
                        (payload.containsKey("name") && payload.containsKey("password") && payload.containsKey("nickNames")))
        ) {
            throw new MuttleySecurityBadRequestException(User.class, null, "Informe o nome, email e ou userName juntamente com a senha")
                    .addDetails(NOME, "Nome completo")
                    .addDetails(USER_NAME, "Informe um userName válido")
                    .addDetails(PASSWD, "Informe uma senha válida")
                    .addDetails(NICK_NAMES, "Informe possíveis nickNames");
        }
        /*if (payload.isEmpty() || payload.size() < 3 || !payload.containsKey(NOME) || !payload.containsKey(USER_NAME) || !payload.containsKey(PASSWD)) {

        }*/

        /*if (payload.size() > 4) {
            throw new MuttleySecurityBadRequestException(User.class, null, "Por favor informe somente o nome, userName, nickNames e a senha")
                    .addDetails(NOME, "Nome completo")
                    .addDetails(USER_NAME, "Informe um userName válido")
                    .addDetails(PASSWD, "Informe uma senha válida")
                    .addDetails(NICK_NAMES, "Informe possíveis nickNames");
        }*/

        final UserPayLoad user = new UserPayLoad(
                (String) payload.get(NOME),
                (String) payload.get(DECRIPTION),
                (String) payload.get(EMAIL),
                (String) payload.get(EMAIL_SECUNDARIO),
                (String) payload.get(USER_NAME),
                (Foto) payload.get(FOTO),
                payload.containsKey(NICK_NAMES) ? new HashSet((List) payload.get(NICK_NAMES)) : null,
                (String) payload.get(PASSWD),
                (String) payload.get(FONE),
                false,
                null,
                (String) payload.get(CODE_VERIFICATION),
                (payload.get(RENEW_CODE_VERIFICATION) == null? false: (Boolean)payload.get(RENEW_CODE_VERIFICATION))
        );
        final User salvedUser = service.save(user, "true");
        if (salvedUser == null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).build();
        } else {
            this.eventPublisher.publishEvent(new UserCreatedEvent(salvedUser));
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
