package br.com.muttley.mongo.autoconfig;

import br.com.muttley.mongo.properties.MuttleyMongoProperties;
import br.com.muttley.mongo.repository.impl.MultiTenancyMongoRepositoryImpl;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Classe de configuração de conexão do mongodb<br/>
 * <p><b> Para realizar a configuração de conexão com o MongoDB, siga o exemplo abaixao</b></p>
 * <ul>
 * <li>Após herdar a classe, torne-a uma classe de configuração com @{@link Configuration}</li>
 * <li>Adicione a anotação @{@link EnableMongoRepositories} e informe onde fica os devidos repositórios em <b>basePackages</b> e também se vier ao caso a classe base em <b>repositoryBaseClass</b> </li>
 * <li>A classe base padrão a ser utilizada é {@link MultiTenancyMongoRepositoryImpl}</li>
 * </ul>
 *
 * @author Joel Rodrigues Moreira on 10/01/18.
 * @project muttley-cloud
 */
@EnableConfigurationProperties(MuttleyMongoProperties.class)
//@ConditionalOnProperty(name = "muttley.mongo.strategy", havingValue = "multitenancy")
@EnableMongoRepositories(repositoryBaseClass = MultiTenancyMongoRepositoryImpl.class)
public class MuttleyMongoMultiTenancyConfig extends MuttleyMongoSimpleTenancyConfig implements InitializingBean {


    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerFactory.getLogger(MuttleyMongoMultiTenancyConfig.class).info(getMessageLog());
    }
}
