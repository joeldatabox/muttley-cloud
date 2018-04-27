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
import java.util.Map;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
public final class ErrorMessage {
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

    public String getMessage() {
        return message;
    }

    public String getObjectName() {
        return objectName;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

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
    protected ResponseEntity<ErrorMessage> toResponseEntity() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE);
        headers.add(RESPONSE_HEADER, "error-message.model.ts");
        return new ResponseEntity(this, headers, this.status);
    }
}
