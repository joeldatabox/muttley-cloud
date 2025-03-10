package br.com.muttley.headers.components.impl;

import br.com.muttley.headers.components.MuttleySerializeType;
import br.com.muttley.headers.model.MuttleyHeader;
import br.com.muttley.model.SerializeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

import static br.com.muttley.model.SerializeType.*;

/**
 * @author Joel Rodrigues Moreira on 29/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Devemos verificar em cada requisição qual o tipo de serialização a ser utilizada
 * SerializeType = 'sync' => devemos serializar o código vindo do serviço do cliente
 * SerializeType = 'ObjectId' => devemos serializar o nosso próprio ObjectId
 * SerializeType = 'ObjectIdAndSync' => devemos serializar o nosso próprio ObjectId juntamente com o sync
 * SerializeType = null => devemos serializar o nosso próprio ObjectId
 * </p>
 */

/**
 * Em 26/02/2025, foram implementadas várias variações do campo "SerializeType"
 * para atender a uma necessidade identificada pelo Eduardo(mobile), que percebeu a importância
 * de permitir o envio do cabeçalho em diferentes formatos.
 * <p>
 * Diante disso, Joel orientou a modificação da classe principal para garantir que
 * valores como "SerializeType", "serializetype", "Serialize-Type" e "serialize-type"
 * fossem aceitos corretamente, evitando problemas de compatibilidade.
 */

@Component("serializeType")
@RequestScope
public class MuttleySerializeTypeImpl extends MuttleyHeader implements MuttleySerializeType {
    private final SerializeType type;

    /*public MuttleySerializeType(@Autowired final ObjectProvider<HttpServletRequest> request) {
        this(request.getIfAvailable());
    }*/

    @Autowired
    public MuttleySerializeTypeImpl(final HttpServletRequest request) {
        super(KEY_FROM_HEADER, request);
        this.type = SerializeType.Builder.build(request);
    }

    @Override
    public boolean isSync() {
        return this.type.isSync();
    }

    @Override
    public boolean isObjectId() {
        return this.type.isObjectId();
    }

    @Override
    public boolean isObjectIdAndSync() {
        return this.type.isObjectIdAndSync();
    }

    @Override
    public boolean isInternal() {
        return this.type.isInternal();
    }

    @Override
    public boolean containsValidValue() {
        return getCurrentValue() != null && (getCurrentValue().equals(SYNC_TYPE) || getCurrentValue().equals(OBJECT_ID_TYPE) || getCurrentValue().equals(OBJECT_ID_AND_SYNC_TYPE));
    }

//    /**
//     * Busca o valor do cabeçalho considerando diferentes variações de nome.
//     */
//    private static String getHeaderValue(HttpServletRequest request, List<String> possibleKeys) {
//        for (String key : possibleKeys) {
//            String value = request.getHeader(key);
//            if (value != null) {
//                return value;
//            }
//        }
//        return null;
//    }
}
