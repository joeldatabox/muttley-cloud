package br.com.muttley.validators.notblank;

import br.com.muttley.validators.MuttleyValidator;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidatorContext;
import java.util.Collection;
import java.util.Map;

/**
 * @author Joel Rodrigues Moreira on 04/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class NotBlankValidator extends MuttleyValidator<NotBlank, Object> {
    @Override
    public boolean isValidValue(final Object value, final ConstraintValidatorContext context) {
        if (value == null) {
            buildMessageError(context);
            return false;
        }

        if (value instanceof String && (StringUtils.isEmpty(value))) {
            buildMessageError(context);
            return false;
        }

        if (value instanceof Collection && (CollectionUtils.isEmpty((Collection<?>) value))) {
            buildMessageError(context);
            return false;
        }

        if (value instanceof Map && (CollectionUtils.isEmpty((Map<?, ?>) value))) {
            buildMessageError(context);
            return false;
        }

        return true;
    }

    private void buildMessageError(final ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(getAnnotation().message())
                .addConstraintViolation();
    }

    @Override
    protected String[] getIgnoreForClients() {
        return this.getAnnotation().ignoreForClients();
    }
}
