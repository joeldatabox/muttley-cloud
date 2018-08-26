package br.com.muttley.domain.autoconfig;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import javax.validation.Validator;

/**
 * Ativa validação na camada de serviço
 */
@Configuration
public class ValidatorConfig implements InitializingBean {

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
    public br.com.muttley.domain.Validator createValidatorFactory(@Autowired final Validator validator) {
        return new br.com.muttley.domain.Validator(validator);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LoggerFactory.getLogger(ValidatorConfig.class).info("Configured BeanValidation");
    }
}
