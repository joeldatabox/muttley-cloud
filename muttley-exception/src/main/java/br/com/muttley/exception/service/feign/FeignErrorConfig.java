package br.com.muttley.exception.service.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 20/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
public class FeignErrorConfig {
    @Bean
    public FeignErrorDecoder createFeignErrorDecoder(@Autowired final ObjectMapper objectMapper) {
        return new FeignErrorDecoder(objectMapper);
    }
}
