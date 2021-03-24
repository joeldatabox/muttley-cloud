package br.com.muttley.redis.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

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
     * @return java.util.Set<String>  -> coleção de chaves salvas no banco
     */
    Set<String> getKey();

    /**
     * @param expression -> expressão basica a ser buscada
     * @return java.util.Set<String>  -> coleção de chaves salvas no banco
     */
    Set<String> getKeys(final String expression);

    /**
     * Salva um objeto qualquer com uma chave especifica
     *
     * @param key   -> chave desejada
     * @param value -> valor a ser salvo
     */
    RedisService set(final String key, final T value);

    /**
     * Salva um objeto qualquer de maneira temporaria
     *
     * @param key   -> chave desejada
     * @param value -> valor a ser salvo
     * @param date  -> data futura que irá expierar o registro
     */
    RedisService set(final String key, final T value, final Date date);

    /**
     * Salva um objeto qualquer de maneira temporaria
     *
     * @param key   -> chave desejada
     * @param value -> valor a ser salvo
     * @param time  -> tempo em milisegundos para se expirar o registro
     */
    RedisService set(final String key, final T value, final long time);

    /**
     * Renomeia uma chave de acesso existente
     *
     * @param currentKey -> chave atual
     * @param newKey     -> nova chave
     */
    RedisService changeKey(final String currentKey, final String newKey);

    /**
     * Recupera um determinado valor qualquer do banco
     *
     * @param key -> chave desejada
     */
    T get(final String key);

    /**
     * Remove um objeto
     *
     * @param key -> key do objeto a ser removido
     */
    RedisService delete(final String key);

    /**
     * Remove um objeto
     *
     * @param expression -> expressão basica das chaves a serem removidas
     */
    RedisService deleteByExpression(final String expression);

    /**
     * lista todos os itens cujo a chave tenha o prefixo {@link #getBasicKey()}
     *
     * @return {@link Collection<Object>}
     */
    Collection list();

    /**
     * Recupera valores do banco de acordo com a expressão regular
     *
     * @param expression -> chave desejada
     */
    List getByExpression(final String expression);

    /**
     * Limpa todos os itens
     */
    RedisService clearAll();

    /**
     * Verifica se existe uma determinada chave no banco
     */
    boolean hasKey(final String key);

    /**
     * Verifica se existe uma determinada chave no banco via expressão regular
     */
    boolean hasKeyByExpression(final String expression);

}
