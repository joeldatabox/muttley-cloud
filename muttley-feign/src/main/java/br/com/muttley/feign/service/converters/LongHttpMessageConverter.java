package br.com.muttley.feign.service;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * @author Joel Rodrigues Moreira on 25/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class LongHttpMessageConverter extends AbstractHttpMessageConverter<Long> {

    public LongHttpMessageConverter() {
        super(MediaType.TEXT_PLAIN);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Long.class.isAssignableFrom(clazz);
    }

    @Override
    protected Long readInternal(Class<? extends Long> clazz, HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        final String requestBody = toString(inputMessage.getBody());
        return requestBody.isEmpty() ? null : Long.valueOf(requestBody);
    }

    @Override
    protected void writeInternal(Long value, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        try {
            final OutputStream outputStream = outputMessage.getBody();
            outputStream.write((value == null ? "" : value.toString()).getBytes());
            outputStream.close();
        } catch (Exception e) {
        }
    }

    private static String toString(InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream);
        final StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.next());
        }
        return stringBuilder.toString();
    }
}