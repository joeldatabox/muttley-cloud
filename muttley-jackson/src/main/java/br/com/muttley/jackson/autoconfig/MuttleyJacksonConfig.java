package br.com.muttley.jackson.autoconfig;

import br.com.muttley.jackson.property.MuttleyJacksonProperty;
import br.com.muttley.jackson.service.MuttleyJacksonDeserializeService;
import br.com.muttley.jackson.service.MuttleyJacksonSerializeService;
import br.com.muttley.jackson.service.infra.MuttleyJacksonDeserialize;
import br.com.muttley.jackson.service.infra.MuttleyJacksonSerialize;
import br.com.muttley.jackson.service.infra.deserializer.ObjectIdDeserializer;
import br.com.muttley.jackson.service.infra.serializer.ObjectIdSerializer;
import br.com.muttley.model.jackson.DefaultDateFormatConfig;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.bson.types.ObjectId;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.text.DateFormat;


/**
 * @author Joel Rodrigues Moreira on 13/03/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
@EnableConfigurationProperties(MuttleyJacksonProperty.class)
public class MuttleyJacksonConfig implements InitializingBean {

    @Value("${spring.jackson.date-format:#{null}}")
    private String dateFormat;
    @Value("${spring.jackson.default-property-inclusion:#{null}}")
    private String propertyInclusionfinal;
    @Autowired
    private MuttleyJacksonProperty property;
    @Autowired
    private ObjectProvider<MuttleyJacksonSerializeService> customizeSerializers;
    @Autowired
    private ObjectProvider<MuttleyJacksonDeserializeService> customizeDeserializers;

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilder() {
        return new Jackson2ObjectMapperBuilderCustomizer() {
            @Override
            public void customize(Jackson2ObjectMapperBuilder mapperBuilder) {
                mapperBuilder.deserializerByType(ObjectId.class, new ObjectIdDeserializer());
                mapperBuilder.serializerByType(ObjectId.class, new ObjectIdSerializer());
                //se não existe um formatador de data padrão, devemos adicionar o nosso
                if (dateFormat == null) {
                    mapperBuilder.dateFormat(createDateFormat());
                }
                //por padrão removeremos campos nulos
                if (propertyInclusionfinal == null) {
                    mapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);
                }
                //adicionando serializers customizados
                customizeSerializers(mapperBuilder);
                //adicionando deserializer customizados
                customizeDeserializers(mapperBuilder);
                //caso algum animal de teta informe uma string no lugar de um objeto, é setado null por padrão
                mapperBuilder.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                        //ignora erro de deserializacao caso uma propriedade não foi encontrada
                        .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            }
        };
    }

    @Bean
    public DateFormat createDateFormat() {
        return new DefaultDateFormatConfig(property.getDatePattern());
    }

    private void customizeSerializers(Jackson2ObjectMapperBuilder mapperBuilder) {
        //Caso exista algum bean no sistema adicionamos
        final MuttleyJacksonSerializeService muttleySerializers = customizeSerializers.getIfAvailable();
        if (muttleySerializers != null) {
            final MuttleyJacksonSerialize[] serializers = muttleySerializers.customizeSerializers();
            if (serializers != null) {
                for (MuttleyJacksonSerialize s : serializers) {
                    mapperBuilder.serializerByType(s.getType(), s.getSerializer());
                }
            }
        }
    }

    private void customizeDeserializers(Jackson2ObjectMapperBuilder mapperBuilder) {
        //Caso exista algum bean no sistema adicionamos
        final MuttleyJacksonDeserializeService muttleyDeserialize = customizeDeserializers.getIfAvailable();
        if (muttleyDeserialize != null) {
            final MuttleyJacksonDeserialize[] deserializes = muttleyDeserialize.customizeDeserializers();
            if (deserializes != null) {
                for (MuttleyJacksonDeserialize d : deserializes) {
                    mapperBuilder.deserializerByType(d.getType(), d.getSerializer());
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerFactory.getLogger(MuttleyJacksonConfig.class).info("Added basic Jackson settings");
    }
}
