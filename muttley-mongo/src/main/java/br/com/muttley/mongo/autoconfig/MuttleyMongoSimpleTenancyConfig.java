package br.com.muttley.mongo.autoconfig;

import br.com.muttley.model.View;
import br.com.muttley.mongo.codec.MuttleyMongoCodec;
import br.com.muttley.mongo.codec.impl.BigDecimalCodec;
import br.com.muttley.mongo.codec.impl.ZonedDateTimeCodec;
import br.com.muttley.mongo.converters.BigDecimalToDecimal128Converter;
import br.com.muttley.mongo.converters.Decimal128ToBigDecimalConverter;
import br.com.muttley.mongo.listeners.VersionSaveMongoEventListener;
import br.com.muttley.mongo.properties.MuttleyMongoProperties;
import br.com.muttley.mongo.repository.impl.MultiTenancyMongoRepositoryImpl;
import br.com.muttley.mongo.repository.impl.SimpleTenancyMongoRepositoryImpl;
import br.com.muttley.mongo.service.MuttleyConvertersService;
import br.com.muttley.mongo.service.MuttleyMongoCodecsService;
import br.com.muttley.mongo.service.MuttleyViewSourceService;
import br.com.muttley.mongo.views.source.ViewSource;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions.MongoConverterConfigurationAdapter;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import java.util.Collection;
import java.util.List;

import static com.mongodb.MongoClientSettings.builder;
import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static com.mongodb.MongoCredential.createCredential;
import static com.mongodb.client.MongoClients.create;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.util.CollectionUtils.isEmpty;

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
public class MuttleyMongoSimpleTenancyConfig extends AbstractMongoClientConfiguration implements InitializingBean {
    @Autowired
    protected MuttleyMongoProperties properties;

    @Autowired
    private ObjectProvider<MuttleyConvertersService> convertersServiceProvider;

    @Autowired
    private ObjectProvider<MuttleyMongoCodecsService> mongoCodecsServiceProvider;

    @Autowired
    private ObjectProvider<MuttleyViewSourceService> viewSourceServiceProvider;


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
        return create(
                builder().applyConnectionString(
                        new ConnectionString("mongodb://" + this.hostDataBase + ":" + portDataBase)
                ).credential(
                        createCredential(this.userName, this.dataBaseName, this.password.toCharArray())
                ).codecRegistry(
                        this.getCodecs()
                ).build()
        );
    }

    @Bean()
    public VersionSaveMongoEventListener userCascadingMongoEventListener() {
        return new VersionSaveMongoEventListener(this.properties);
    }

    @Override
    protected String getDatabaseName() {
        return this.dataBaseName;
    }

    @Override
    protected void configureConverters(MongoConverterConfigurationAdapter adapter) {
        adapter.registerConverter(new BigDecimalToDecimal128Converter());
        adapter.registerConverter(new Decimal128ToBigDecimalConverter());

        //pegando instancia do serviço de conversores caso exista
        final MuttleyConvertersService convertersService = convertersServiceProvider.getIfAvailable();
        if (convertersService != null) {
            //pegando o conversores customizados
            final Collection<? extends Converter> converters = convertersService.getCustomConverters();
            //dicionando conversores personalizados
            if (!isEmpty(converters)) {
                adapter.registerConverters(converters);
            }
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final MuttleyViewSourceService service = this.viewSourceServiceProvider.getIfAvailable();
        if (service != null) {
            this.createViews(service.getCustomViewSource());
        }
        LoggerFactory.getLogger(MuttleyMongoSimpleTenancyConfig.class).info(getMessageLog());
    }

    private CodecRegistry getCodecs() {
        //pegando os codecs customizados que foram implementados no servidor
        final MuttleyMongoCodecsService service = this.mongoCodecsServiceProvider.getIfAvailable();
        final List<CodecProvider> customProviders = service != null ? service.getCustomCodecs().stream().map(MuttleyMongoCodec::getCodecProvider).collect(toList()) : null;

        return !isEmpty(customProviders) ?
                fromRegistries(
                        asList(getDefaultCodecRegistry(),
                                fromProviders(
                                        asList(
                                                new BigDecimalCodec().getCodecProvider(),
                                                new ZonedDateTimeCodec().getCodecProvider()
                                        )
                                ),
                                fromProviders(customProviders)
                        )
                ) :
                fromRegistries(
                        asList(getDefaultCodecRegistry(),
                                fromProviders(
                                        asList(
                                                new BigDecimalCodec().getCodecProvider(),
                                                new ZonedDateTimeCodec().getCodecProvider()
                                        )
                                )
                        )
                );
    }

    private void createViews(final ViewSource[] sources) throws Exception {
        if (sources != null && sources.length > 0) {

            final MongoClient client = this.mongoClient();
            final MongoTemplate template = new MongoTemplate(client, this.dataBaseName);


            for (final ViewSource source : sources) {
                final Query query = new Query();
                query.addCriteria(where("name").is(source.getViewName()));
                //verificando se a view existe
                final View view = template.findOne(new Query(where("name").is(source.getViewName())), View.class);


                if (view == null) {
                    //a view não existe, logo devemos criar a mesma
                    client
                            .getDatabase(this.dataBaseName)
                            .createView(source.getViewName(), source.getViewOn(), source.getPipeline());
                    //salvando informações da view criada
                    template.save(new View(source.getViewName(), source.getVersion(), source.getDescription()));
                } else {
                    //se a view já existe devemos verificar a versão da mesma
                    //se a versão for diferente devemos dropar essa view
                    if (!view.getVersion().equals(source.getVersion())) {
                        client
                                .getDatabase(this.dataBaseName)
                                .getCollection(source.getViewName())
                                .drop();

                        //adicionando novamente a view
                        client
                                .getDatabase(this.dataBaseName)
                                .createView(source.getViewName(), source.getViewOn(), source.getPipeline());

                        //atualizando info da view
                        //this.template.save(view.updateInfo(source));
                        template.save(view.setDescription(source.getDescription())
                                .setVersion(source.getVersion()));
                    }
                }
            }
            client.close();
        }
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
