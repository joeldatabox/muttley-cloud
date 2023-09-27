package br.com.muttley.notification.infobip.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 18/09/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
@ComponentScan(basePackages = "br.com.muttley.notification.infobip.service")
@EnableFeignClients(basePackages = "br.com.muttley.notification.infobip.service.impl")
public class InfobipConfig {
}
