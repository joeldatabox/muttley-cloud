package br.com.muttley.feign.service.config;

import feign.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 20/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
public class FeignTimeoutConfig {
    @Value("${muttley.config.feign.connectTimeOutMillis:60000}")
    private int connectTimeOutMillis;
    @Value("${muttley.config.feign.readTimeOutMillis:60000}")
    private int readTimeOutMillis;

    @Bean
    public Request.Options options() {
        return new Request.Options(connectTimeOutMillis, readTimeOutMillis);
    }
}
