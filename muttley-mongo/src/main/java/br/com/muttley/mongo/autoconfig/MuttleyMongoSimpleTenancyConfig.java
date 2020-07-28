package br.com.muttley.mongo.autoconfig;

import br.com.muttley.model.View;
import br.com.muttley.mongo.codec.MuttleyMongoCodec;
import br.com.muttley.mongo.codec.impl.BigDecimalCodec;
import br.com.muttley.mongo.converters.BigDecimalToDecimal128Converter;
import br.com.muttley.mongo.converters.Decimal128ToBigDecimalConverter;
import br.com.muttley.mongo.properties.MuttleyMongoProperties;
import br.com.muttley.mongo.repository.impl.MultiTenancyMongoRepositoryImpl;
import br.com.muttley.mongo.repository.impl.SimpleTenancyMongoRepositoryImpl;
import br.com.muttley.mongo.service.MuttleyConvertersService;
import br.com.muttley.mongo.service.MuttleyMongoCodecsService;
import br.com.muttley.mongo.service.MuttleyViewSourceService;
import br.com.muttley.mongo.views.source.ViewSource;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.bson.codecs.configuration.CodecRegistry;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactory;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.MongoClient.getDefaultCodecRegistry;
import static com.mongodb.MongoCredential.createCredential;
import static java.util.Collections.singletonList;
import static org.bson.BSON.addEncodingHook;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.springframework.data.mongodb.core.query.Criteria.where;

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
        return new MongoClient(
                singletonList(new ServerAddress(this.hostDataBase, portDataBase)),
                createCredential(this.userName, this.dataBaseName, this.password.toCharArray()),
                getMongoClientOption()
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
        final MuttleyConvertersService convertersService = convertersServiceProvider.getIfAvailable();
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
        final MuttleyViewSourceService service = this.viewSourceServiceProvider.getIfAvailable();
        if (service != null) {
            this.createViews(service.getCustomViewSource());
        }
        LoggerFactory.getLogger(MuttleyMongoSimpleTenancyConfig.class).info(getMessageLog());
    }

    private MongoClientOptions getMongoClientOption() {
        //registrando os codecs básicos
        final BigDecimalCodec bigDecimalCodec = new BigDecimalCodec();
        final ZonedDateTimeTra

        addEncodingHook(bigDecimalCodec.getEncoderClass(), bigDecimalCodec.getTransformer());
        //lista para armazenar os registros de codecs
        final List<CodecRegistry> codecRegistries = new ArrayList();
        //adicionando o codec para bigdecimal
        codecRegistries.add(fromProviders(bigDecimalCodec.getCodecProvider()));


        //pegando os codecs customizados que foram implementados no servidor
        final MuttleyMongoCodecsService service = this.mongoCodecsServiceProvider.getIfAvailable();
        if (service != null) {
            final MuttleyMongoCodec[] customCondecs = service.getCustomCodecs();
            if (customCondecs != null) {
                for (final MuttleyMongoCodec codec : customCondecs) {
                    //adicionando no bson
                    addEncodingHook(codec.getEncoderClass(), codec.getTransformer());

                    //adicionando na lista
                    codecRegistries.add(fromProviders(codec.getCodecProvider()));
                }
            }
        }

        //adicionando codecs basicos
        codecRegistries.add(getDefaultCodecRegistry());

        return MongoClientOptions
                .builder()
                .codecRegistry(
                        fromRegistries(codecRegistries)
                ).build();
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
