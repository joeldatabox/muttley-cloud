package br.com.muttley.hermes.server.autoconfig.mongo;

import br.com.muttley.mongo.repository.impl.SimpleTenancyMongoRepositoryImpl;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * @author Joel Rodrigues Moreira on 22/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Realiza a configuração do mongo db
 */
@Configuration
@EntityScan(basePackages = "br.com.muttley.model.hermes")
@EnableMongoRepositories(basePackages = {"br.com.muttley.hermes.server.repository"}, repositoryBaseClass = SimpleTenancyMongoRepositoryImpl.class)
public class MuttleyMongoSimpleTenancyConfig extends br.com.muttley.mongo.autoconfig.MuttleyMongoSimpleTenancyConfig {

}
