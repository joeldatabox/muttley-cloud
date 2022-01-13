package br.com.muttley.model.util;

import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 12/01/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class MapUtils {

    public static Object getValueByNavigation(final String key, final Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        //se tem ponto, logo devemo fazer navegação por nível
        if (key.contains(".")) {
            //pegando a primeira chave
            final String firstKey = key.substring(0, key.indexOf("."));
            //pegando o objeto referente a chave para recursão
            final Object item = map.get(firstKey);
            //verificando se item é um map
            //caso não for devemos retornar null
            if (!(item instanceof Map)) {
                return null;
            }
            //pegando o item recuperado e fazendo recursão para pegar o proxímo nível
            return getValueByNavigation(key.substring(key.indexOf(".") + 1), (Map<String, Object>) item);
        }
        return map.get(key);
    }
}
