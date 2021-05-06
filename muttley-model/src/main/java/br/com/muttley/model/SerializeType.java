package br.com.muttley.model;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Joel Rodrigues Moreira on 06/05/2021.
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

public class SerializeType {
    public static final String KEY_FROM_HEADER = "SerializeType";
    public static final String KEY_INTERNAL_FROM_HEADER = "SerializeTypeInternal";

    public static final String SYNC_TYPE = "sync";
    public static final String OBJECT_ID_TYPE = "ObjectId";
    public static final String OBJECT_ID_AND_SYNC_TYPE = "ObjectIdAndSync";

    //public static final SerializeType2 INTERNAL = new SerializeType2("true");


    private final String value;
    private boolean internal;

    private SerializeType(final String value) {
        this.value = value;
    }

    private SerializeType(final String value, final boolean internal) {
        this(value);
        this.internal = internal;
    }

    public boolean isInternal() {
        return internal;
    }

    protected SerializeType setInternal(final boolean value) {
        this.internal = internal;
        return this;
    }

    protected SerializeType setInternal(final String value) {
        return value == null ? this.setInternal(false) : setInternal("true".equals(value));
    }

    public boolean isSync() {
        return SYNC_TYPE.equals(this.value);
    }

    public boolean isObjectId() {
        return OBJECT_ID_TYPE.equals(this.value);
    }

    public boolean isObjectIdAndSync() {
        return OBJECT_ID_AND_SYNC_TYPE.equals(this.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SerializeType that = (SerializeType) o;
        return internal == that.internal && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, internal);
    }

    public static class Builder {
        //private HttpServletRequest request;
        private String type;
        private boolean internal = false;

        private Builder() {
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder setRequest(final HttpServletRequest request) {
            return setType(request == null ? null : request.getHeader(KEY_FROM_HEADER));
        }

        public Builder setType(final String type) {
            this.type = type;
            return this;
        }

        private Builder setInternal(final String value) {
            this.internal = "true".equals(value);
            return this;
        }

        public SerializeType build() {
            if (this.type == null) {
                return new SerializeType(SerializeType.OBJECT_ID_TYPE);
            }
            final SerializeType serializeType;
            switch (type) {
                case SerializeType.SYNC_TYPE:
                case SerializeType.OBJECT_ID_AND_SYNC_TYPE:
                case SerializeType.OBJECT_ID_TYPE:
                    serializeType = new SerializeType(type, false);
                    break;
                default:
                    serializeType = new SerializeType(SerializeType.OBJECT_ID_TYPE, false);
            }
            serializeType.setInternal(this.internal);
            return serializeType;
        }

        public static SerializeType build(final HttpServletRequest request) {
            return Builder.newInstance().setRequest(request).build();
        }


    }
}
