package br.com.muttley.mongo.service;

import br.com.muttley.mongo.service.converters.BigDecimalToDecimal128Converter;
import br.com.muttley.mongo.service.converters.Decimal128ToBigDecimalConverter;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.MongoCredential.createCredential;
import static java.util.Collections.singletonList;

/**
 * Classe de configuração de conexão do mongodb<br/>
 * <p><b> Para realizar a configuração de conexão com o MongoDB, siga o exemplo abaixao</b></p>
 * <ul>
 * <li>Após herdar a classe, torne-a uma classe de configuração com @{@link org.springframework.context.annotation.Configuration}</li>
 * <li>Adicione a anotação @{@link org.springframework.data.mongodb.repository.config.EnableMongoRepositories} e informe onde fica os devidos repositórios em <b>basePackages</b> e também se vier ao caso a classe base em <b>repositoryBaseClass</b> </li>
 * <li>A classe base padrão a ser utilizada é {@link br.com.muttley.mongo.service.repository.impl.CustomMongoRepositoryImpl}</li>
 * </ul>
 *
 * @author Joel Rodrigues Moreira on 10/01/18.
 * @project demo
 */
//@Configuration
//@EnableMongoRepositories(basePackages = "br.com", repositoryBaseClass = CustomMongoRepositoryImpl.class)
public class MongoConfig extends AbstractMongoConfiguration {
    private final CustomConversions converters;

    protected final String dataBaseName;
    protected final String hostDataBase;
    protected final String portDataBase;
    protected final String userName;
    protected final String password;

    public MongoConfig(@Value("${spring.data.mongodb.database}") final String dataBaseName,
                       @Value("${spring.data.mongodb.host:localhost}") final String hostDataBase,
                       @Value("${spring.data.mongodb.port:27017}") final String portDataBase,
                       @Value("${spring.data.mongodb.username:}") final String userName,
                       @Value("${spring.data.mongodb.password:}") final String password) {

        this.dataBaseName = dataBaseName;
        this.hostDataBase = hostDataBase;
        this.portDataBase = portDataBase;
        this.userName = userName;
        this.password = password;

        final List listConverters = new ArrayList(2);
        listConverters.add(new BigDecimalToDecimal128Converter());
        listConverters.add(new Decimal128ToBigDecimalConverter());

        this.converters = new CustomConversions(listConverters);
    }

    @Override
    protected String getDatabaseName() {
        return dataBaseName;
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        return new MongoClient(
                singletonList(new ServerAddress(this.hostDataBase, Integer.valueOf(this.portDataBase))),
                singletonList(createCredential(this.userName, this.dataBaseName, password.toCharArray())));
    }

    @Override
    public CustomConversions customConversions() {
        return this.converters;
    }

    @Bean
    public MongoRepositoryFactory getMongoRepositoryFactory() {
        try {
            return new MongoRepositoryFactory(this.mongoTemplate());
        } catch (Exception e) {
            throw new RuntimeException("error creating mongo repository factory", e);
        }
    }

}
