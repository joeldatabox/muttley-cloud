package br.com.muttley.feign.service.converters;

import br.com.muttley.model.security.Authority;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserDataBinding;
import br.com.muttley.model.security.preference.UserPreferences;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 26/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class UserHttpMessageConverter extends MuttleyHttpMessageConverter<User> {

    public UserHttpMessageConverter() {
        super(MediaType.APPLICATION_JSON);
    }

    @Override
    protected boolean supports(final Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    @Override
    protected User readInternal(final Class<? extends User> clazz, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        final ObjectMapper mapper = new ObjectMapper()
                .registerModule(
                        new SimpleModule("ObjectId",
                                new Version(1, 0, 0, null, null, null)
                        ).addDeserializer(User.class, new UserSerializer())
                );
        return mapper.readValue(inputMessage.getBody(), User.class);
    }

    @Override
    protected void writeInternal(final User user, final HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputMessage.getBody(), user);
    }

    private static class UserSerializer extends JsonDeserializer<User> {
        @Override
        public User deserialize(final JsonParser parser, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
            final ObjectCodec codec = parser.getCodec();
            final JsonNode node = codec.readTree(parser);
            if (node.isNull()) {
                return null;
            }
            return new User().setId(node.get("id").asText())
                    .setName(node.get("name").asText())
                    .setDescription(node.get("description").asText())
                    .setEmail(node.get("email").asText())
                    .setUserName(node.get("userName").asText())
                    .setNickUsers(node.get("nickUsers").traverse(codec).readValueAs(new TypeReference<Set<String>>() {
                    })).setEnable(node.get("enable").asBoolean())
                    .setAuthorities((Set<Authority>) node.get("authorities").traverse(codec).readValueAs(new TypeReference<Set<Authority>>() {
                    })).setPreferences(node.get("preferences").traverse(codec).readValueAs(UserPreferences.class))
                    .setDataBindings(node.get("preferences").traverse(codec).readValueAs(new TypeReference<List<UserDataBinding>>() {
                    })).setOdinUser(node.get("odinUser").asBoolean());
        }
    }
}
