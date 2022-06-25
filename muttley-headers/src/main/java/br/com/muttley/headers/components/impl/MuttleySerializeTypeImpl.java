package br.com.muttley.headers.components.impl;

import br.com.muttley.headers.components.MuttleySerializeType;
import br.com.muttley.headers.model.MuttleyHeader;
import br.com.muttley.model.SerializeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

import static br.com.muttley.model.SerializeType.KEY_FROM_HEADER;
import static br.com.muttley.model.SerializeType.OBJECT_ID_AND_SYNC_TYPE;
import static br.com.muttley.model.SerializeType.OBJECT_ID_TYPE;
import static br.com.muttley.model.SerializeType.SYNC_TYPE;

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

}
