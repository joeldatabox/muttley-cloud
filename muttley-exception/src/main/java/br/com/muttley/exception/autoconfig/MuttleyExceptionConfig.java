package br.com.muttley.exception.autoconfig;

import br.com.muttley.exception.handlers.CustomResponseEntityExceptionHandler;
import br.com.muttley.exception.ErrorMessageBuilder;
import br.com.muttley.exception.controllers.ConfigEndPointsErros;
import br.com.muttley.exception.controllers.ErrorsController;
import br.com.muttley.exception.feign.FeignErrorDecoder;
import br.com.muttley.exception.property.MuttleyExceptionProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 18/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
@ComponentScan(basePackages = {
        "br.com.muttley.exception.controllers",
        "br.com.muttley.exception.feign",
        "br.com.muttley.exception.handlers"
})
@EnableConfigurationProperties(MuttleyExceptionProperty.class)
public class MuttleyExceptionConfig {

    /*@Bean
    public FeignErrorDecoder createFeignErrorDecoder(@Autowired final ObjectMapper objectMapper) {
        return new FeignErrorDecoder(objectMapper);
    }*/

    @Bean
    public ErrorMessageBuilder errorMessageBuilderFactory() {
        return new ErrorMessageBuilder();
    }

    /*@Bean
    public ConfigEndPointsErros configEndPointsErrosFactory() {
        return new ConfigEndPointsErros();
    }*/

    /*@Bean
    public ErrorsController errorsControllerFactory() {
        return new ErrorsController();
    }*/

    /*@Bean
    public CustomResponseEntityExceptionHandler customResponseEntityExceptionHandlerFactory(@Autowired final ErrorMessageBuilder errorMessageBuilder) {
        return new CustomResponseEntityExceptionHandler(errorMessageBuilder);
    }*/
}
