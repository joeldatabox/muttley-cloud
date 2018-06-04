package br.com.muttley.exception.service;

import br.com.muttley.exception.service.serializer.HttpStatusDeserializer;
import br.com.muttley.exception.service.serializer.HttpStatusSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
public final class ErrorMessage {
    @JsonIgnore
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String RESPONSE_HEADER_VALUE = "error-message.model.ts";
    @JsonSerialize(using = HttpStatusSerializer.class)
    @JsonDeserialize(using = HttpStatusDeserializer.class)
    protected HttpStatus status;
    protected String message;
    protected String objectName;
    protected final Map<String, Object> details;
    @JsonIgnore
    public static final String RESPONSE_HEADER = "error-message";

    public ErrorMessage() {
        this.details = new HashMap<>();
    }

    @JsonCreator
    public ErrorMessage(
            @JsonProperty("status") final HttpStatus status,
            @JsonProperty("message") final String message,
            @JsonProperty("objectName") final String objectName,
            @JsonProperty("details") final Map<String, Object> details) {

        this.status = status;
        this.message = message;
        this.objectName = objectName;
        this.details = details;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public ErrorMessage setStatus(final HttpStatus status) {
        this.status = status;
        return this;
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
            return new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @JsonIgnore
    protected ResponseEntity toResponseEntity() {
        return toResponseEntity(new HttpHeaders());
    }

    @JsonIgnore
    protected ResponseEntity toResponseEntity(final HttpHeaders headers) {
        headers.add(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE);
        headers.add(RESPONSE_HEADER, RESPONSE_HEADER_VALUE);
        return new ResponseEntity(this, headers, this.status);
    }
}
