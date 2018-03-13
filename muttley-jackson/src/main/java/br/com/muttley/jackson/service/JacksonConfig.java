package br.com.muttley.jackson.service;

import br.com.muttley.jackson.service.infra.MuttleyJacksonDeserialize;
import br.com.muttley.jackson.service.infra.MuttleyJacksonSerialize;
import br.com.muttley.jackson.service.infra.deserializer.ObjectIdDeserializer;
import br.com.muttley.jackson.service.infra.serializer.ObjectIdSerializer;
import br.com.muttley.model.jackson.DefaultDateFormatConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;


/**
 * @author Joel Rodrigues Moreira on 13/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
public class JacksonConfig {

    @Value("${spring.jackson.date-format:#{null}}")
    private String dateFormat;
    @Value("${spring.jackson.default-property-inclusion:#{null}}")
    private String propertyInclusionfinal;
    @Value("${br.com.muttley.jackson.date-pattern:yyyy-MM-dd'T'HH:mm:ss.SSSZ}")
    private String datePattern;


    @Bean
    public Jackson2ObjectMapperBuilderCustomizer addCustomBigDecimalDeserialization() {
        return new Jackson2ObjectMapperBuilderCustomizer() {
            @Override
            public void customize(Jackson2ObjectMapperBuilder mapperBuilder) {
                mapperBuilder.deserializerByType(ObjectId.class, new ObjectIdDeserializer());
                mapperBuilder.serializerByType(ObjectId.class, new ObjectIdSerializer());
                //se não existe um formatador de data padrão, devemos adicionar o nosso
                if (dateFormat == null) {
                    mapperBuilder.dateFormat(new DefaultDateFormatConfig(datePattern));
                }
                //por padrão removeremos campos nulos
                if (propertyInclusionfinal == null) {
                    mapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);
                }
                final MuttleyJacksonSerialize[] serializers = customizeSerializers();
                if (serializers != null) {
                    for (MuttleyJacksonSerialize s : serializers) {
                        mapperBuilder.serializerByType(s.getType(), s.getSerializer());
                    }
                }
                final MuttleyJacksonDeserialize[] deserializes = customizeDeserializers();
                if (deserializes != null) {
                    for (MuttleyJacksonDeserialize d : deserializes) {
                        mapperBuilder.deserializerByType(d.getType(), d.getSerializer());
                    }
                }
            }
        };
    }

    public MuttleyJacksonDeserialize[] customizeDeserializers() {
        return null;
    }

    public MuttleyJacksonSerialize[] customizeSerializers() {
        return null;
    }

}
