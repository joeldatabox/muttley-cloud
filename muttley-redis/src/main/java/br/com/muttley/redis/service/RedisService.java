package br.com.muttley.redis.service;

import java.util.Collection;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 08/01/18.
 * @project demo
 */
public interface RedisService {
    /**
     * @return String -> prefixo da chave usada
     */
    String getBasicKey();

    /**
     * Salva um objeto qualquer
     *
     * @param value -> valor a ser salvo
     */
    void set(Object value);

    /**
     * Salva um objeto qualquer de maneira temporaria
     *
     * @param value -> valor a ser salvo
     * @param time  -> tempo em segundos para se expirar o registro
     */
    void set(Map value, long time);

    /**
     * Salva um objeto qualquer com uma chave especifica
     *
     * @param key   -> chave desejada
     * @param value -> valor a ser salvo
     */
    void set(String key, Object value);

    /**
     * Salva um objeto qualquer de maneira temporaria
     *
     * @param key   -> chave desejada
     * @param value -> valor a ser salvo
     * @param time  -> tempo em milisegundos para se expirar o registro
     */
    void set(String key, Map value, long time);

    /**
     * Recupera um determinado valor qualquer do banco
     *
     * @param key -> chave desejada
     */
    Object get(String key);

    /**
     * Remove um objeto
     *
     * @param key -> key do objeto a ser removido
     */
    void delete(String key);

    /**
     * lista todos os itens cujo a chave tenha o prefixo {@link #getBasicKey()}
     *
     * @return {@link Collection<Object>}
     */
    Collection<Object> list();

    /**
     * Limpa todos os itens
     */
    void clearAll();

}
