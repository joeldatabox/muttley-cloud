package br.com.muttley.feign.service.converters;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira on 25/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class LongHttpMessageConverter extends MuttleyHttpMessageConverter<Long> {

    public LongHttpMessageConverter() {
        super(MediaType.TEXT_PLAIN);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Long.class.isAssignableFrom(clazz);
    }

    @Override
    protected Long readInternal(Class<? extends Long> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        final String requestBody = toString(inputMessage.getBody());
        return requestBody.equalsIgnoreCase("null") ? null : Long.valueOf(requestBody);
    }

    @Override
    protected void writeInternal(Long value, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        this.writeString(value == null ? "null" : value.toString(), outputMessage);
    }
}