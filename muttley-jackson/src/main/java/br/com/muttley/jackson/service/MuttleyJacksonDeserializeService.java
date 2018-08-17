package br.com.muttley.jackson.service;

import br.com.muttley.jackson.service.infra.MuttleyJacksonDeserialize;

public interface MuttleyJacksonDeserializeService {
    public MuttleyJacksonDeserialize[] customizeDeserializers();
}
