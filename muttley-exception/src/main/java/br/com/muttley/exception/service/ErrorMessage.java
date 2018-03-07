package br.com.muttley.exception.service;

import br.com.muttley.exception.service.serializer.HttpStatusSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
public final class ErrorMessage {
    @JsonSerialize(using = HttpStatusSerializer.class)
    protected HttpStatus status;
    protected String message;
    protected String objectName;
    protected final Map<String, Object> details;
    private static final String RESPONSE_HEADER = "ERROR-MESSAGE";

    public ErrorMessage() {
        this.details = new HashMap<>();
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

    @JsonIgnore
    protected final String toJson() {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @JsonIgnore
    protected ResponseEntity<ErrorMessage> toResponseEntity() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString());
        headers.add(RESPONSE_HEADER,"error-message.model.ts");
        return new ResponseEntity(this, headers, this.status);
    }
}
