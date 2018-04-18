package br.com.muttley.model.security.resource;

import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.UriTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Created by joel on 14/04/17.
 */
public class UserResource extends ResourceSupport {
    private Resource<User> user;

    public UserResource(final User user) {
        this.user = new Resource<>(user);
        this.user.add(getSelfLink());
    }

    public UserResource(final JwtUser user) {
        this(user.getOriginUser());
    }

    public UserResource(final Authentication authentication) {
        this((JwtUser) authentication.getPrincipal());
    }

    public Resource<User> getUser() {
        return user;
    }

    @JsonIgnore
    private Link getSelfLink() {
        final UriTemplate uri = new UriTemplate(
                ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .buildAndExpand()
                        .toUri().toASCIIString());
        return new Link(uri, "self");
    }
}
