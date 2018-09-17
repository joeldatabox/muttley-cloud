package br.com.muttley.jackson.service;

import br.com.muttley.jackson.service.infra.MuttleyJacksonSerialize;

public interface MuttleyJacksonSerializeService {
    public MuttleyJacksonSerialize[] customizeSerializers();
}
