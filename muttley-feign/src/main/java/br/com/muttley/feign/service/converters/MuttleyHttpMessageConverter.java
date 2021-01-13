package br.com.muttley.feign.service.converters;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @author Joel Rodrigues Moreira on 21/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class MuttleyHttpMessageConverter<T> extends AbstractHttpMessageConverter<T> implements HttpMessageConverter<T> {
    public MuttleyHttpMessageConverter() {
    }

    public MuttleyHttpMessageConverter(final MediaType supportedMediaType) {
        super(supportedMediaType);
    }

    public MuttleyHttpMessageConverter(final MediaType... supportedMediaTypes) {
        super(supportedMediaTypes);
    }

    public MuttleyHttpMessageConverter(final Charset defaultCharset, final MediaType... supportedMediaTypes) {
        super(defaultCharset, supportedMediaTypes);
    }

    protected String toString(InputStream inputStream) {
        try (final Scanner scanner = new Scanner(inputStream)) {
            final StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNext()) {
                stringBuilder.append(scanner.next());
            }
            return stringBuilder.toString();
        }
    }

    protected void writeString(final String value, final HttpOutputMessage outputMessage) throws IOException {
        try (final OutputStream outputStream = outputMessage.getBody()) {
            outputStream.write(value.getBytes());
        }
    }
}
