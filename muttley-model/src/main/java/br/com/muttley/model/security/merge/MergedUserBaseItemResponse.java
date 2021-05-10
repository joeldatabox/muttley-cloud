package br.com.muttley.model.security.merge;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 10/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "email")
public class MergedUserBaseItemResponse {
    private String email;
    private Status status;
    private Map<String, String> details;

    public MergedUserBaseItemResponse() {
    }

    public MergedUserBaseItemResponse(final String email) {
        this.email = email;
    }

    public MergedUserBaseItemResponse(final String email, final Status status) {
        this(email);
        this.status = status;
        this.details = new HashMap<>();
    }

    @JsonCreator
    public MergedUserBaseItemResponse(
            @JsonProperty("email") final String email,
            @JsonProperty("status") final Status status,
            @JsonProperty("details") final Map<String, String> details) {
        this(email, status);
        this.details = details;
    }

    public MergedUserBaseItemResponse addDetails(final String key, final String details) {
        if (this.details == null) {
            this.details = new HashMap<>();
        }
        this.details.put(key, details);
        return this;
    }
}
