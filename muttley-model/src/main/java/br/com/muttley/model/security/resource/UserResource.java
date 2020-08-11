package br.com.muttley.model.security.resource;

import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Created by joel on 14/04/17.
 */
public class UserResource extends RepresentationModel {
    @Getter
    private EntityModel<User> user;

    public UserResource(final User user) {
        this.user = EntityModel.of(user);
        this.user.add(getSelfLink());
    }

    public UserResource(final JwtUser user) {
        this(user.getOriginUser());
    }

    public UserResource(final Authentication authentication) {
        this((JwtUser) authentication.getPrincipal());
    }

    @JsonIgnore
    private Link getSelfLink() {
        return Link.of(ServletUriComponentsBuilder
                        .fromCurrentRequest()
                        .buildAndExpand()
                        .toUri()
                        .toASCIIString()
                , "self");
    }
}
