package br.com.muttley.security.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//Packages onde existem entidades
@EntityScan(basePackages = {"br.com.muttley.model.security"})
//Packages onde existem componentes, serviços e configurações
@ComponentScan(basePackages = {
        //Injeções internas do projeto
        "br.com.muttley.domain.service"
})
//@EnableEurekaClient
public class MuttleySecurityServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MuttleySecurityServerApplication.class, args);
    }

}
