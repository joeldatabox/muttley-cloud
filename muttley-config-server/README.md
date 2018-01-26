## Configurando seu "service-config"

Adicione a dependência em seu serviço de configuração;

	<dependency>
		<groupId>br.com</groupId>
        <artifactId>muttley-config-server</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>

Para poder ativar a configuração basica de segurança no serviço faça em sua classe main  como está no exemplo abaixo:

    import org.springframework.boot.SpringApplication;
	import org.springframework.boot.autoconfigure.SpringBootApplication;
	import org.springframework.cloud.config.server.EnableConfigServer;
	import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
	import org.springframework.context.annotation.ComponentScan;

	@SpringBootApplication
    @EnableConfigServer
    @EnableEurekaClient
    @ComponentScan(basePackages = {"br.com.muttley.configserver.service"})
    public class ConfigServerApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(SpringConfigApplication.class, args);
        }
    }

Não se esqueça de configurar também em seu resource as seguintes variáveis de ambiente:

    muttley.config-server.security.user.name
    muttley.config-server.security.user.password
    muttley.config-server.security.user.role

Essas variáveis serão utilizadas para que os demais serviços possa se comunicar com o serviço de configuração de manera segura;
