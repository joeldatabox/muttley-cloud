package br.com.muttley.validators.size;

import br.com.muttley.validators.MuttleyValidator;

import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 04/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class SizeValidator extends MuttleyValidator<Size, Object> {
    @Override
    protected boolean isValidValue(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            if (!getAnnotation().nullable()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(getAnnotation().message())
                        .addConstraintViolation();
                return false;
            }
            return true;
        } else {
            final int size = getLenght(value);

            if (!(size <= getAnnotation().max() && size >= getAnnotation().min())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(getAnnotation().message())
                        .addConstraintViolation();
                return false;
            }
            return true;
        }
    }

    @Override
    protected String[] getIgnoreForClients() {
        return this.getAnnotation().ignoreForClients();
    }

    protected int getLenght(final Object value) {
        if (value instanceof Collection) {
            return ((Collection) value).size();
        }

        if (value.getClass().isArray()) {
            return Array.getLength(value);
        }

        if (value instanceof Map) {
            return ((Map) value).size();
        }

        if (value instanceof CharSequence) {
            return ((CharSequence) value).length();
        }

        throw new IllegalArgumentException("Tipo de dado não tratado ");
    }
}
