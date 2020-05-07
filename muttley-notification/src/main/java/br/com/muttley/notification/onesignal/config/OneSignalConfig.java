package br.com.muttley.notification.onesignal.config;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "br.com.muttley.notification.onesignal.service")
@EnableFeignClients(
        basePackages = "br.com.muttley.notification.onesignal.service.impl"
)
public class OneSignalConfig {
}
