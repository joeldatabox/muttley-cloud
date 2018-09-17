package br.com.muttley.exception;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyConflictException;
import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.exception.throwables.MuttleyMethodNotAllowedException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotAcceptableException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.exception.throwables.security.MuttleySecurityCredentialException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 04/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class ExceptionBuilder {

    public static Exception buildException(final Response response, final ObjectMapper mapper) {
        if (response.headers().containsKey(ErrorMessage.RESPONSE_HEADER)) {
            try {
                switch (HttpStatus.valueOf(response.status())) {
                    case INTERNAL_SERVER_ERROR:
                        return new MuttleyException(getErrorMessage(response, mapper));
                    case BAD_REQUEST:
                        return new MuttleyBadRequestException(getErrorMessage(response, mapper));
                    case CONFLICT:
                        return new MuttleyConflictException(getErrorMessage(response, mapper));
                    case FORBIDDEN:
                        return new MuttleySecurityCredentialException(getErrorMessage(response, mapper));
                    case NOT_FOUND:
                        return new MuttleyNotFoundException(getErrorMessage(response, mapper));
                    case UNAUTHORIZED:
                        return new MuttleySecurityUnauthorizedException(getErrorMessage(response, mapper));
                    case METHOD_NOT_ALLOWED:
                        return new MuttleyMethodNotAllowedException(getErrorMessage(response, mapper));
                    case NO_CONTENT:
                        return new MuttleyNoContentException(getErrorMessage(response, mapper));
                    case NOT_ACCEPTABLE:
                        return new MuttleyNotAcceptableException(getErrorMessage(response, mapper));
                    default:
                        return new MuttleyException(getErrorMessage(response, mapper));
                }
            } catch (final IOException ex) {
                return new MuttleyException(ex);
            }
        }
        return null;
    }

    private static ErrorMessage getErrorMessage(final Response response, final ObjectMapper mapper) throws IOException {
        if (response.body() != null) {
            final ErrorMessage message = mapper.readValue(response.body().asInputStream(), ErrorMessage.class);
            return message.setCustomMapper(mapper);
        }
        return new ErrorMessage()
                .setCustomMapper(mapper)
                .setStatus(response.status());
    }
}
