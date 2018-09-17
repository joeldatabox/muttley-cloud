package br.com.muttley.redis.service;

import java.util.Collection;

/**
 * @author Joel Rodrigues Moreira on 08/01/18.
 * @project demo
 */
public interface RedisService<T> {
    /**
     * @return String -> prefixo da chave usada
     */
    String getBasicKey();

    /**
     * Salva um objeto qualquer com uma chave especifica
     *
     * @param key   -> chave desejada
     * @param value -> valor a ser salvo
     */
    RedisService set(String key, T value);

    /**
     * Salva um objeto qualquer de maneira temporaria
     *
     * @param key   -> chave desejada
     * @param value -> valor a ser salvo
     * @param time  -> tempo em milisegundos para se expirar o registro
     */
    RedisService set(String key, T value, long time);

    /**
     * Recupera um determinado valor qualquer do banco
     *
     * @param key -> chave desejada
     */
    T get(String key);

    /**
     * Remove um objeto
     *
     * @param key -> key do objeto a ser removido
     */
    RedisService delete(String key);

    /**
     * lista todos os itens cujo a chave tenha o prefixo {@link #getBasicKey()}
     *
     * @return {@link Collection<Object>}
     */
    Collection<T> list();

    /**
     * Limpa todos os itens
     */
    RedisService clearAll();

    /**
     * Verifica se existe uma determinada chave no banco
     */
    boolean hasKey(final String key);

}
