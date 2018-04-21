package br.com.muttley.exception.service.feign;

import br.com.muttley.exception.service.ErrorMessage;
import br.com.muttley.exception.throwables.MuttleyException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static br.com.muttley.exception.service.ErrorMessage.RESPONSE_HEADER;

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
        if (response.headers().containsKey(RESPONSE_HEADER)) {
            try {
                throw new MuttleyException(
                        objectMapper.readValue(response.body().asInputStream(), ErrorMessage.class)
                );
            } catch (final IOException ex) {
                throw new MuttleyException(ex);
            }
        }
        return new Default().decode(methodKey, response);
    }
}
