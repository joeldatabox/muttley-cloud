package br.com.muttley.security.server.controller;
import br.com.muttley.model.security.AccessPlan;
import br.com.muttley.security.server.service.AccessPlanService;
import br.com.muttley.security.server.service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Joel Rodrigues Moreira on 23/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/access-plan", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class AccessPlanController extends AbstractRestController<AccessPlan, ObjectId> {

    @Autowired
    public AccessPlanController(final AccessPlanService service, final UserService userService, final ApplicationEventPublisher eventPublisher) {
        super(service, userService, eventPublisher);
    }

    @Override
    protected ObjectId deserializerId(final String id) {
        return new ObjectId(id);
    }
}
