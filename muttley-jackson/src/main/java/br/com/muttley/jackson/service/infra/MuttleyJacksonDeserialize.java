package br.com.muttley.jackson.service.infra;

import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * @author Joel Rodrigues Moreira on 13/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyJacksonDeserialize<T> {
    private final Class<T> type;
    private final JsonDeserializer<T> deserializer;

    public MuttleyJacksonDeserialize(final Class<T> type, final JsonDeserializer<T> deserializer) {
        this.type = type;
        this.deserializer = deserializer;
    }

    public Class<T> getType() {
        return type;
    }

    public JsonDeserializer<T> getSerializer() {
        return deserializer;
    }
}
