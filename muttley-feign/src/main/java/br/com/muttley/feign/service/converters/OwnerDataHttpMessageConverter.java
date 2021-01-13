package br.com.muttley.feign.service.converters;

import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.OwnerDataImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

/**
 * @author Joel Rodrigues Moreira 12/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class OwnerDataHttpMessageConverter extends MuttleyHttpMessageConverter<OwnerData> {

    public OwnerDataHttpMessageConverter() {
        super(MediaType.APPLICATION_JSON);
    }

    @Override
    protected boolean supports(final Class<?> clazz) {
        return OwnerData.class.isAssignableFrom(clazz);
    }

    @Override
    protected OwnerData readInternal(final Class<? extends OwnerData> clazz, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        final ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputMessage.getBody(), new TypeReference<OwnerDataImpl>() {
        });
    }

    @Override
    protected void writeInternal(final OwnerData ownerData, final HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputMessage.getBody(), ownerData);
    }
}
