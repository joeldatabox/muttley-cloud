package br.com.muttley.muttleydiscoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MuttleyDiscoveryServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MuttleyDiscoveryServerApplication.class, args);
	}
}
