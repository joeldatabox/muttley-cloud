package br.com.muttley.security.feign.formatter;

import br.com.muttley.model.security.KeyUserDataBinding;
import org.springframework.cloud.netflix.feign.FeignFormatterRegistrar;
import org.springframework.format.FormatterRegistry;
import org.springframework.stereotype.Component;

/**
 * @author Joel Rodrigues Moreira 26/02/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Component
public class KeyUserDataBindingFormatterRegistrar implements FeignFormatterRegistrar {
    @Override
    public void registerFormatters(final FormatterRegistry formatterRegistry) {
        formatterRegistry.addConverter(KeyUserDataBinding.class, String.class, keyUserDataBinding -> keyUserDataBinding.getKey());
    }
}
