package br.com.muttley.hermes.server.autoconfig;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 04/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
@ComponentScan(basePackages = {
        "br.com.muttley.hermes.server.autoconfig.mongo",
        "br.com.muttley.hermes.server.controller",
        "br.com.muttley.hermes.server.service"
})
public class MuttleyHermesAutoconfig {
}
