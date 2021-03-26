package br.com.muttley.model.security;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 25/03/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class KeyUserDataBindingAvaliable implements KeyUserDataBinding {
    private static final Map<String, KeyUserDataBinding> cache = new HashMap<>();
    @Getter
    private final String key;

    protected KeyUserDataBindingAvaliable(String key) {
        this.key = key;
        cache.put(key, this);
    }

    public static KeyUserDataBinding from(final String value) {
        return cache.get(value);
    }
}
