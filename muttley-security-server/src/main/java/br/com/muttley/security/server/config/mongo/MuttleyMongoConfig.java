package br.com.muttley.security.server.config.mongo;

import br.com.muttley.mongo.repository.impl.DocumentMongoRepositoryImpl;
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
@EnableMongoRepositories(basePackages = {"br.com.muttley.security.server.repository"}, repositoryBaseClass = DocumentMongoRepositoryImpl.class)
public class MuttleyMongoConfig extends br.com.muttley.mongo.autoconfig.MuttleyMongoConfig {
}
