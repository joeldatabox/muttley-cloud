package br.com.muttley.exception.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;

import static br.com.muttley.exception.ExceptionBuilder.buildException;

/**
 * @author Joel Rodrigues Moreira on 20/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class FeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Autowired
    public FeignErrorDecoder(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(final String methodKey, final Response response) {
        final Exception exception = buildException(response, objectMapper);
        return exception != null ? exception : new Default().decode(methodKey, response);
    }
}
