package br.com.muttley.model.security;

import br.com.muttley.model.security.jackson.KeyUserDataBindingDeserializer;
import br.com.muttley.model.security.jackson.KeyUserDataBindingSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 10/02/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@JsonSerialize(using = KeyUserDataBindingSerializer.class)
@JsonDeserialize(using = KeyUserDataBindingDeserializer.class)
public interface KeyUserDataBinding {

    String getDisplayKey();

    /**
     * Chave a ser usada
     */
    String getKey();

    /**
     * Indica se a chave pode esta correlacionada a mais de um usuário por owner
     */
    boolean isUnique();

    static KeyUserDataBinding from(final String value) {
        return Avaliable.from(value);
    }

    public static class Avaliable {
        private static final Set<KeyUserDataBinding> cache = new HashSet<>();

        public static void add(final KeyUserDataBinding value) {
            if (value != null) {
                cache.add(value);
            }
        }

        public static Set<KeyUserDataBinding> getValues() {
            return Avaliable.cache;
        }

        public static KeyUserDataBinding from(final String value) {
            return getValues().parallelStream()
                    .filter(it -> it.getKey().equals(value))
                    .findFirst()
                    .orElse(null);
        }
    }
}
