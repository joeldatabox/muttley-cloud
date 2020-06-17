package br.com.muttley.redis.model;

import lombok.Getter;

/**
 * @author Joel Rodrigues Moreira 17/06/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
public class MuttleyRedisWrapper<T> {
    private T content;

    public MuttleyRedisWrapper() {
    }

    public MuttleyRedisWrapper(final T content) {
        this.content = content;
    }
}
