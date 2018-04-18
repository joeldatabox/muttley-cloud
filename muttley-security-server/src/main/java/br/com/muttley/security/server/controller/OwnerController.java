package br.com.muttley.security.server.controller;

import br.com.muttley.model.Owner;
import br.com.muttley.security.server.service.OwnerService;
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
@RequestMapping(value = "/api/v1/owners", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class OwnerController extends AbstractRestController<Owner, ObjectId> {

    @Autowired
    public OwnerController(final OwnerService service, final UserService userService, final ApplicationEventPublisher eventPublisher) {
        super(service, userService, eventPublisher);
    }

    @Override
    protected ObjectId deserializerId(final String id) {
        return new ObjectId(id);
    }
}
