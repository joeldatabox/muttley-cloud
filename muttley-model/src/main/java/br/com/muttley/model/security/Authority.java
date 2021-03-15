package br.com.muttley.model.security;

import org.springframework.security.core.GrantedAuthority;

public interface Authority {

    Role getRole();

    String getDescription();

    default GrantedAuthority toGrantedAuthority() {
        return new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return getRole().toString();
            }
        };
    }
}
