package br.com.muttley.model.security.converters;

import br.com.muttley.model.security.KeyUserDataBinding;
import org.springframework.core.convert.converter.Converter;

/**
 * @author Joel Rodrigues Moreira 10/02/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class KeyUserDataBindingToStringConverter implements Converter<KeyUserDataBinding, String> {


    @Override
    public String convert(final KeyUserDataBinding source) {
        return source == null ? null : source.getKey();
    }
}
