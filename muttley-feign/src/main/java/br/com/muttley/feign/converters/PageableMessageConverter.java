package br.com.muttley.feign.converters;

import br.com.muttley.model.jackson.converter.ObjectIdSerializer;
import br.com.muttley.rest.hateoas.resource.PageableResource;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.bson.types.ObjectId;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.PROPERTY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL;

/**
 * @author Joel Rodrigues Moreira on 29/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class PageableMessageConverter extends MuttleyHttpMessageConverter<PageableResource> {
    @Override
    protected boolean supports(Class<?> clazz) {
        return PageableResource.class.isAssignableFrom(clazz);
    }

    @Override
    protected PageableResource readInternal(Class<? extends PageableResource> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return getObjectMapper().readValue(inputMessage.getBody(), clazz);
    }

    @Override
    protected void writeInternal(PageableResource pageableResource, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        this.writeString(getObjectMapper().writeValueAsString(pageableResource), outputMessage);
    }

    private static ObjectMapper getObjectMapper() {
        return new ObjectMapper()
                .enableDefaultTyping(NON_FINAL, PROPERTY)
                .registerModule(
                        new SimpleModule("ObjectId",
                                new Version(1, 0, 0, null, null, null)
                        ).addSerializer(org.bson.types.ObjectId.class, new ObjectIdSerializer())
                )
                .setVisibility(FIELD, ANY);
    }
}
