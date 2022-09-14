package br.com.muttley.localcache.services;

import br.com.muttley.model.security.APIToken;

public interface LocalAPITokenService {
    public static final String BASIC_KEY = "API-TOKEN:";

    APIToken loadAPIToken(final String token);

    LocalAPITokenService expireAPIToken(final String token);
}
