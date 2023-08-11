package br.com.muttley.files.config;

import br.com.muttley.files.properties.Properties;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.nio.file.Paths;

/**
 * @author Joel Rodrigues Moreira on 28/07/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
@EnableAsync
@ComponentScan(basePackages = "br.com.muttley.files.listeners")
@EnableConfigurationProperties(Properties.class)
public class MuttleyFilesConfig implements InitializingBean {
    @Autowired
    private Properties properties;

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerFactory.getLogger(MuttleyFilesConfig.class).info("File caching service has been configured: \n\t" + Paths.get(properties.getFiles()).toAbsolutePath().toString());
    }
}

