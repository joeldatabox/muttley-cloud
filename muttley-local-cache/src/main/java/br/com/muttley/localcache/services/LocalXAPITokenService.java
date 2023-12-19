package br.com.muttley.localcache.services;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.XAPIToken;

public interface LocalXAPITokenService {
    static enum Type {
        XAPIToken,
        JWTToken
    }

    public static final String BASIC_KEY = "API-TOKEN:";

    XAPIToken loadAPIToken(final String token);

    JwtToken loadJwtTokenFrom(final String xAPIToken);

    LocalXAPITokenService expireAPIToken(final String token);
}
