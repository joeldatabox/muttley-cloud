package br.com.muttley.hermes.server.config;


import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration()
@ComponentScan(basePackages = {
        "br.com.muttley.hermes.server.config.model",
        "br.com.muttley.hermes.server.config.mongo"
})
@EnableFeignClients(
        basePackages = "br.com.muttley.notification.onesignal.service"
)
public class Config {
}
