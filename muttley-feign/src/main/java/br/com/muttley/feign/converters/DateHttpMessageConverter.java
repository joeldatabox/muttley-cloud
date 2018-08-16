package br.com.muttley.feign.converters;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 25/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class DateHttpMessageConverter extends MuttleyHttpMessageConverter<Date> {

    public DateHttpMessageConverter() {
        super(MediaType.TEXT_PLAIN);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return Date.class.isAssignableFrom(clazz);
    }

    @Override
    protected Date readInternal(Class<? extends Date> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        try {
            final String requestBody = toString(inputMessage.getBody());
            return requestBody.equalsIgnoreCase("null") ? null : newDateFormat().parse(requestBody);
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    @Override
    protected void writeInternal(Date value, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        this.writeString(value == null ? "null" : newDateFormat().format(value), outputMessage);
    }

    private DateFormat newDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }
}