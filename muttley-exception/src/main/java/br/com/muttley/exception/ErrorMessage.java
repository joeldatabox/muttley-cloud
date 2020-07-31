package br.com.muttley.exception;

import br.com.muttley.exception.serializer.HttpStatusDeserializer;
import br.com.muttley.exception.serializer.HttpStatusSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.util.StringUtils.isEmpty;
import static org.springframework.web.servlet.HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@JsonPropertyOrder({"message", "field", "objectName", "details", "status"})
public final class ErrorMessage {
    @JsonIgnore
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    @JsonIgnore
    public static final String RESPONSE_HEADER_VALUE = "error-message.model.ts";
    @JsonSerialize(using = HttpStatusSerializer.class)
    @JsonDeserialize(using = HttpStatusDeserializer.class)
    protected HttpStatus status;
    protected String field;
    protected String message;
    protected String objectName;
    protected final Map<String, Object> details;
    @JsonIgnore
    protected ObjectMapper customMapper;
    @JsonIgnore
    public static final String RESPONSE_HEADER = "error-message";

    public ErrorMessage() {
        this.details = new HashMap<>();
    }

    @JsonCreator
    public ErrorMessage(
            @JsonProperty("field") final String field,
            @JsonProperty("status") final HttpStatus status,
            @JsonProperty("message") final String message,
            @JsonProperty("objectName") final String objectName,
            @JsonProperty("details") final Map<String, Object> details) {
        this.field = field;
        this.status = status;
        this.message = message;
        this.objectName = objectName;
        this.details = details;
    }

    public String getField() {
        return field;
    }

    public ErrorMessage setField(final String field) {
        this.field = field;
        return this;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorMessage setStatus(final HttpStatus status) {
        this.status = status;
        return this;
    }

    public ErrorMessage setStatus(final int status) {
        return this.setStatus(HttpStatus.valueOf(status));
    }

    public String getMessage() {
        return message;
    }

    public ErrorMessage setMessage(final String message) {
        this.message = message;
        return this;
    }

    public String getObjectName() {
        return objectName;
    }

    public ErrorMessage setObjectName(final String objectName) {
        this.objectName = objectName;
        return this;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    private ObjectMapper getObjectMapper() {
        if (customMapper == null) {
            this.customMapper = new ObjectMapper();
        }
        return customMapper;
    }

    public ErrorMessage setCustomMapper(ObjectMapper customMapper) {
        this.customMapper = customMapper;
        return this;
    }

    public static String getResponseHeader() {
        return RESPONSE_HEADER;
    }

    @JsonIgnore
    public ErrorMessage concatMessage(final String message) {
        if (isEmpty(this.message)) {
            this.message = message;
        } else {
            this.message += LINE_SEPARATOR + message;
        }
        return this;
    }

    @JsonIgnore
    public ErrorMessage addDetails(final String key, Object value) {
        this.details.put(key, value);
        return this;
    }

    @JsonIgnore
    public ErrorMessage addDetails(final String key, final Object... value) {
        this.details.put(key, value);
        return this;
    }

    @JsonIgnore
    public ErrorMessage addDetails(final String key, final List<Object> value) {
        this.details.put(key, value);
        return this;
    }

    @JsonIgnore
    public ErrorMessage addDetails(final Map<String, Object> details) {
        this.details.putAll(details);
        return this;
    }

    @JsonIgnore
    public boolean containsDetails() {
        return details != null && !details.isEmpty();
    }

    @JsonIgnore
    protected final String toJson() {
        try {
            return getObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @JsonIgnore
    public ResponseEntity toResponseEntity() {
        return toResponseEntity(new HttpHeaders());
    }

    @JsonIgnore
    public ResponseEntity toResponseEntity(final HttpHeaders headers) {
        return this.toResponseEntity(true, headers);
    }

    @JsonIgnore
    public ResponseEntity toResponseEntity(boolean serializeResponse, final HttpHeaders headers) {
        headers.add(RESPONSE_HEADER, RESPONSE_HEADER_VALUE);
        if (serializeResponse) {
            headers.add(CONTENT_TYPE, APPLICATION_JSON_VALUE);
            return new ResponseEntity(this, headers, this.status);
        }
        return ResponseEntity.status(this.status).headers(headers).build();
    }

    @JsonIgnore
    public ResponseEntity toResponseEntity(final HttpServletRequest request) {
        //verificando se tem algum Media type que possa retornar json
        if (((LinkedHashSet<MediaType>) request.getAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE)).stream()
                .filter(it -> APPLICATION_JSON.equals(it) || APPLICATION_JSON_UTF8.equals(it) || ALL_VALUE.equals(it))
                .count() > 0) {
            //se chegou aqui quer dizer que podemo retornar um json,
            //vamos remover qualquer coisa que possa dar outra exception relacionada a media type
            request.removeAttribute(PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
            return this.toResponseEntity(true, new HttpHeaders());
        }
        return this.toResponseEntity(false, new HttpHeaders());
    }

}
