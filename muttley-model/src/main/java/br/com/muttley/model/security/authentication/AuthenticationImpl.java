package br.com.muttley.model.security.authentication;

import br.com.muttley.model.security.Authority;
import br.com.muttley.model.security.AuthorityImpl;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserDataBinding;
import br.com.muttley.model.security.XAPIToken;
import br.com.muttley.model.security.jackson.UserDeserializer;
import br.com.muttley.model.security.jackson.UserSerializer;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.model.workteam.WorkTeamDomain;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * @author Joel Rodrigues Moreira on 19/09/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class AuthenticationImpl implements Authentication {
    @JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)
    private User currentUser;
    @Transient
    private Owner currentOwner;
    @Transient
    private WorkTeamDomain workTeam;
    @Transient
    private Set<Authority> authorities;
    @Transient
    private List<UserDataBinding> dataBindings;
    @Transient
    private UserPreferences preferences;
    private boolean odinUser = false;

    public AuthenticationImpl() {
        this.authorities = new HashSet<>();
        this.dataBindings = new ArrayList<>();
    }

    public AuthenticationImpl(final XAPIToken xapiToken) {
        this.currentUser = xapiToken.getUser();
        this.currentOwner = xapiToken.getOwner();
        //this.workTeam = xapiToken.get
        this.authorities = new HashSet<>();
        this.dataBindings = new ArrayList<>();
    }


    public final boolean inRole(final Authority role) {
        return getAuthorities().contains(role);
    }

    public final boolean inAnyRole(final String... roles) {
        return inAnyRole(Stream.of(roles).map(AuthorityImpl::new));
    }

    public final boolean inAnyRole(final Role... roles) {
        return this.inAnyRole(Stream.of(roles).map(AuthorityImpl::new));
    }

    public final boolean inAnyRole(final Authority... roles) {
        return inAnyRole(Stream.of(roles));
    }

    public final boolean inAnyRole(final Stream<Authority> roles) {
        return roles.filter(it -> it != null).anyMatch(getAuthorities()::contains);
    }

    @Override
    public Authentication setAuthorities(Set<Authority> authorities) {
        this.authorities = authorities;
        return this;
    }

    @Override
    public Authentication setAuthorities(final Collection<Role> roles) {
        this.authorities = roles.parallelStream().map(it -> new AuthorityImpl(it)).collect(toSet());
        return this;
    }

    @Override
    public boolean isOdiUser() {
        return odinUser;
    }

    @Override
    public Set<Authority> getAuthorities() {
        return authorities;
    }

    /////////////////////////////////
    /*@Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.getCurrentUser().getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isOdiUser() {
        return false;
    }*/
}
