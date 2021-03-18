package br.com.muttley.model.security;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 17/03/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class KeyUserDataBindingAvaliable implements KeyUserDataBinding {
    private final String key;
    public static Map<String, KeyUserDataBinding> cache = new HashMap<>();

    public KeyUserDataBindingAvaliable(String key) {
        this.key = key;

        KeyUserDataBindingAvaliable.cache.put(key, this);
    }

    public static KeyUserDataBinding from(final String value) {
        if (KeyUserDataBindingAvaliable.cache.containsKey(value)) {
            return KeyUserDataBindingAvaliable.cache.get(value);
        }
        return null;
    }
}
