package br.com.muttley.security.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//Packages onde existem entidades
@EntityScan(basePackages = {"br.com.muttley.model.security"})
//Packages onde existem componentes, serviços e configurações
@ComponentScan(basePackages = {
        //Injeções internas do projeto
        "br.com.muttley.domain.service",
        //Configuração de serviços
        "br.com.muttley.security.server",
        //Configurações de segurança para o gateway
        //"br.com.muttley.security.zuul.gateway.service",
        //Configurações de exceptions
        "br.com.muttley.exception.service",
        //Configurações de serialização
        "br.com.muttley.jackson.service"
})
//@EnableEurekaClient
public class MuttleySecurityServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MuttleySecurityServerApplication.class, args);
    }

}
