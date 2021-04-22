package br.com.muttley.admin.server;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira 22/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
//Packages onde existem entidades
@EntityScan(basePackages = {"br.com.muttley.model.admin"})
@ComponentScan(basePackages = {
        //Injeções internas do projeto
        "br.com.muttley.admin.server",
        //Configuração de serviços
        "br.com.muttley.domain.service",
        //Configurações de segurança para o gateway
        "br.com.muttley.security.zuul.gateway.service",
        //Configurações do serviço de cache
        "br.com.muttley.redis.service",
        //Configurações de exceptions
        "br.com.muttley.exception.service",
        //Configurações de serialização
        "br.com.muttley.jackson.service",
        "br.com.muttley.feign.service",
        "br.com.muttley.rest"
})
@EnableEurekaClient
@EnableFeignClients(basePackages = "br.com.muttley.security.feign")
public class MuttleyAdminServerConfig {
}
