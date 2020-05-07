package br.com.muttley.hermes.api.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(
        basePackages = "br.com.muttley.hermes.api"
)
public class MuttleyHermesAPIConfig {
}
