package br.com.muttley.localcache.services;

import br.com.muttley.model.security.XAPIToken;

public interface LocalXAPITokenService {
    public static final String BASIC_KEY = "API-TOKEN:";

    XAPIToken loadAPIToken(final String token);

    LocalXAPITokenService expireAPIToken(final String token);
}
