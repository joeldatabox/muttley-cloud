package br.com.muttley.files.config;

import br.com.muttley.files.properties.Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Joel Rodrigues Moreira on 28/07/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
@EnableAsync
@ComponentScan(basePackages = "br.com.muttley.files.listeners")
@EnableConfigurationProperties(Properties.class)
public class MuttleyFilesConfig {
    private String folder = "./muttley-cloud-files/";
}
