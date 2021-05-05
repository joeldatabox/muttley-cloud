package br.com.muttley.headers.components;

import br.com.muttley.headers.model.MuttleyHeader;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

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
public class MuttleySerializeType extends MuttleyHeader {
    public static final String key = "SerializeType";
    public static final String keyInternal = "SerializeTypeInternal";
    private static final String SYNC_TYPE = "sync";
    private static final String OBJECT_ID_TYPE = "ObjectId";
    private static final String OBJECT_ID_AND_SYNC_TYPE = "ObjectIdAndSync";
    private final String currentValueInternal;

    public MuttleySerializeType(@Autowired final ObjectProvider<HttpServletRequest> request) {
        this(request.getIfAvailable());
    }

    public MuttleySerializeType(final HttpServletRequest request) {
        super(key, request);
        if (request != null) {
            this.currentValueInternal = request.getHeader(keyInternal);
        } else {
            this.currentValueInternal = null;
        }
    }

    public boolean isSync() {
        return SYNC_TYPE.equals(getCurrentValue());
    }

    public boolean isObjectId() {
        return OBJECT_ID_TYPE.equals(getCurrentValue()) || getCurrentValue() == null;
    }

    public boolean isObjectIdAndSync() {
        return OBJECT_ID_AND_SYNC_TYPE.equals(getCurrentValue());
    }

    public boolean isInternal() {
        return "true".equals(this.currentValueInternal);
    }

    public boolean containsValidValue() {
        return getCurrentValue() != null && (getCurrentValue().equals(SYNC_TYPE) || getCurrentValue().equals(OBJECT_ID_TYPE) || getCurrentValue().equals(OBJECT_ID_AND_SYNC_TYPE));
    }

}
