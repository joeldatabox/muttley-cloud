package br.com.muttley.hermes.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
        "br.com.muttley.hermes.server",
        //"br.com.muttley.hermes.server.config.mongo",
        //"br.com.muttley.hermes.server.service",
        "br.com.muttley.notification.onesignal.config"
})
public class MuttleyHermesServerConfig {
}
