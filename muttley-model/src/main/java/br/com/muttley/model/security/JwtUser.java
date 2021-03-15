package br.com.muttley.model.security;

import br.com.muttley.model.jackson.JsonHelper;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.lang.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Joel Rodrigues Moreira on 08/01/18.
 * @project demo
 */
public class JwtUser implements UserDetails {

    private final String id;
    private final String name;

    private final Password password;
    private final String username;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean enabled;
    private final Date lastPasswordResetDate;
    /*@Value("${springboot..security.jwt.issuer}")
    private String issuer ;*/
    private final User originUser;

    public JwtUser(final Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.password = builder.password;
        this.username = builder.userName;
        this.authorities = builder.authorities;
        this.enabled = builder.enabled;
        this.lastPasswordResetDate = builder.lastPasswordResetDate;
        this.originUser = builder.originUser;
    }

    @JsonCreator
    public JwtUser(
            @JsonProperty("id") final String id,
            @JsonProperty("name") final String name,
            @JsonProperty("password") final Password password,
            @JsonProperty("username") final String userName,
            @JsonProperty("authorities") final Collection<? extends GrantedAuthority> authorities,
            @JsonProperty("enabled") final boolean enabled,
            @JsonProperty("lastPasswordResetDate") final Date lastPasswordResetDate,
            @JsonProperty("originUser") final User originUser) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.username = userName;
        this.authorities = authorities;
        this.enabled = enabled;
        this.lastPasswordResetDate = lastPasswordResetDate;
        this.originUser = originUser;
    }

    @JsonIgnore
    public User getOriginUser() {
        return originUser;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (Collections.isEmpty(this.authorities)) {
            return mapToGrantedAuthorities(this.originUser.getAuthorities());
        }
        return authorities;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @JsonIgnore
    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    private static final List<GrantedAuthority> mapToGrantedAuthorities(final Collection<Authority> authorities) {
        return authorities.parallelStream()
                .map(authority -> new SimpleGrantedAuthority(authority.getRole().toString()))
                .collect(Collectors.toList());
    }

    public String toJson() {
        return JsonHelper.toJson(this);
    }

    public static class Builder {
        private String id;
        private String name;
        private Password password;
        private String userName;
        private Collection<? extends GrantedAuthority> authorities;
        private boolean enabled;
        private Date lastPasswordResetDate;
        private User originUser;

        private Builder() {

        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder setId(final String id) {
            this.id = id;
            return this;
        }

        public Builder setName(final String name) {
            this.name = name;
            return this;
        }

        public Builder setPassword(final Password password) {
            this.password = password;
            return this;
        }

        public Builder setUserName(final String userName) {
            this.userName = userName;
            return this;
        }

        public Builder setAuthorities(final Collection<? extends GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public Builder setEnabled(final boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder setLastPasswordResetDate(final Date lastPasswordResetDate) {
            this.lastPasswordResetDate = lastPasswordResetDate;
            return this;
        }

        public Builder setOriginUser(final User originUser) {
            this.originUser = originUser;
            return this;
        }

        public Builder set(final User user) {
            return this.setId(user.getId())
                    .setName(user.getName())
                    .setUserName(user.getUserName())
                    .setAuthorities(user.getAuthorities().parallelStream().map(Authority::toGrantedAuthority).collect(Collectors.toList()))
                    .setEnabled(user.isEnable())
                    .setOriginUser(user);
        }

        public JwtUser build() {
            return new JwtUser(this.id, this.name, this.password, this.userName, this.authorities, this.enabled, this.lastPasswordResetDate, this.originUser);
        }
    }
}
