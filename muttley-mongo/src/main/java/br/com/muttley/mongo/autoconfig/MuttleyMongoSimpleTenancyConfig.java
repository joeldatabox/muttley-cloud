package br.com.muttley.mongo.autoconfig;

import br.com.muttley.mongo.converters.BigDecimalToDecimal128Converter;
import br.com.muttley.mongo.converters.Decimal128ToBigDecimalConverter;
import br.com.muttley.mongo.properties.MuttleyMongoProperties;
import br.com.muttley.mongo.repository.impl.MultiTenancyMongoRepositoryImpl;
import br.com.muttley.mongo.repository.impl.SimpleTenancyMongoRepositoryImpl;
import br.com.muttley.mongo.service.MuttleyConvertersService;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
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
 * <li>A classe base padrão a ser utilizada é {@link MultiTenancyMongoRepositoryImpl}</li>
 * </ul>
 *
 * @author Joel Rodrigues Moreira on 10/01/18.
 * @project muttley-cloud
 */
@EnableConfigurationProperties(MuttleyMongoProperties.class)
//@ConditionalOnProperty(name = "muttley.mongo.strategy", havingValue = "simpletenancy", matchIfMissing = true)
@EnableMongoRepositories(repositoryBaseClass = SimpleTenancyMongoRepositoryImpl.class)
public class MuttleyMongoSimpleTenancyConfig extends AbstractMongoConfiguration implements InitializingBean {
    @Autowired
    protected MuttleyMongoProperties properties;

    @Autowired
    private ObjectProvider<MuttleyConvertersService> convertersSErviceProvider;


    @Value("${spring.data.mongodb.database}")
    protected String dataBaseName;
    @Value("${spring.data.mongodb.host:localhost}")
    protected String hostDataBase;
    @Value("${spring.data.mongodb.port:27017}")
    protected int portDataBase;
    @Value("${spring.data.mongodb.username:}")
    protected String userName;
    @Value("${spring.data.mongodb.password:}")
    protected String password;

    @Override
    public MongoClient mongoClient() {
        return new MongoClient(
                singletonList(new ServerAddress(this.hostDataBase, portDataBase)),
                createCredential(this.userName, this.dataBaseName, this.password.toCharArray()),
                MongoClientOptions
                        .builder()
                        .build()
        );
    }

    @Override
    protected String getDatabaseName() {
        return this.dataBaseName;
    }

    @Override
    public final org.springframework.data.convert.CustomConversions customConversions() {
        //pegando os conversores padrão
        final List converters = new ArrayList(2);
        converters.add(new BigDecimalToDecimal128Converter());
        converters.add(new Decimal128ToBigDecimalConverter());

        //pegando instancia do serviço de conversores caso exista
        final MuttleyConvertersService convertersService = convertersSErviceProvider.getIfAvailable();
        if (convertersService != null) {
            //pegando o conversores customizados
            final Converter[] customConversions = convertersService.getCustomConverters();
            //dicionando conversores personalizados
            if (customConversions != null && customConversions.length > 0) {
                for (Converter con : customConversions) {
                    converters.add(con);
                }
            }
        }
        return new MongoCustomConversions(converters);
    }


    @Bean
    public MongoRepositoryFactory getMongoRepositoryFactory() {
        try {
            return new MongoRepositoryFactory(this.mongoTemplate());
        } catch (Exception e) {
            throw new RuntimeException("error creating mongo repository factory", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerFactory.getLogger(MuttleyMongoSimpleTenancyConfig.class).info(getMessageLog());
    }

    protected String getMessageLog() {
        final StringBuilder builder = new StringBuilder("Configure MongoDB using ");
        if (properties.getStrategy().isMultiTenancyDocument()) {
            builder.append("MultiTenancyStrategy ");
        } else {
            builder.append("SimpleTenancyStrategy ");
        }

        builder.append("\"").append(this.hostDataBase).append("\", port \"").append(this.portDataBase).append("\", data base \"").append(getDatabaseName()).append("\", with userName \"").append(this.userName).append("\" and password \"");
        if (this.password != null) {
            final char[] passwdLenght = this.password.toCharArray();
            for (int i = 0; i < passwdLenght.length; i++) {
                builder.append('*');
            }
        }
        builder.append("\"");
        return this.toString() + " - " + builder.toString();
    }
}
