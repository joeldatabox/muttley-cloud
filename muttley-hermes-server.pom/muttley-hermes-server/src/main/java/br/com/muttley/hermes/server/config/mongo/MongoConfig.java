package br.com.muttley.hermes.server.config.mongo;

import br.com.muttley.model.security.converters.KeyUserDataBindingToStringConverter;
import br.com.muttley.model.security.converters.StringToKeyUserDataBindingConverter;
import br.com.muttley.mongo.service.repository.impl.DocumentMongoRepositoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author Joel Rodrigues Moreira on 30/04/20.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project agrifocus-cloud
 * <p>
 * Realiza a configuração do mongo db
 */
@Configuration
@EnableMongoRepositories(basePackages = {"br.com.muttley.hermes.server.repository"}, repositoryBaseClass = DocumentMongoRepositoryImpl.class)
public class MongoConfig extends br.com.muttley.mongo.service.MongoConfig {

    private final ApplicationEventPublisher publisher;

    public MongoConfig(@Value("${spring.data.mongodb.database}") final String dataBaseName,
                       @Value("${spring.data.mongodb.host}") final String hostDataBase,
                       @Value("${spring.data.mongodb.port}") final String portDataBase,
                       @Value("${spring.data.mongodb.username}") final String userName,
                       @Value("${spring.data.mongodb.password}") final String password,
                       final ApplicationEventPublisher publisher) {
        super(dataBaseName, hostDataBase, portDataBase, userName, password);
        this.publisher = publisher;
    }
    @Override
    protected Converter[] getConverters() {
        return new Converter[]{
                new StringToKeyUserDataBindingConverter(this.publisher),
                new KeyUserDataBindingToStringConverter()
        };
    }
}
