package br.com.muttley.domain.autoconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validator;

/**
 * Ativa validação na camada de serviço
 */
@Configuration
public class ValidatorConfig {

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessorFactory() {
        final MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
        processor.setValidator(validatorFactory());
        return processor;
    }

    @Bean
    public Validator validatorFactory() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public br.com.muttley.domain.Validator createValidatorFactory() {
        return new br.com.muttley.domain.Validator(validatorFactory());
    }
}
