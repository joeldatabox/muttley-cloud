package br.com.muttley.jackson.service.infra;

import com.fasterxml.jackson.databind.JsonSerializer;

/**
 * @author Joel Rodrigues Moreira on 13/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MuttleyJacksonSerialize<T> {
    private final Class<T> type;
    private final JsonSerializer<T> serializer;

    public MuttleyJacksonSerialize(final Class<T> type, final JsonSerializer<T> serializer) {
        this.type = type;
        this.serializer = serializer;
    }

    public Class<T> getType() {
        return type;
    }

    public JsonSerializer<T> getSerializer() {
        return serializer;
    }
}
