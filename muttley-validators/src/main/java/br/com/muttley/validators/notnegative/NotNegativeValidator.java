package br.com.muttley.validators.notnegative;

import br.com.muttley.validators.MuttleyValidator;

import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;

/**
 * @author Joel Rodrigues Moreira on 04/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class NotNegativeValidator extends MuttleyValidator<NotNegative, Object> {
    @Override
    protected boolean isValidValue(final Object value, final ConstraintValidatorContext context) {
        if (getAnnotation().nullable() && value == null) {
            return true;
        }

        if ((!getAnnotation().nullable()) && value == null) {
            return buildMessageError(context);
        }


        final BigDecimal number = new BigDecimal(String.valueOf(value));

        if (number.compareTo(BigDecimal.ZERO) < 0) {
            return buildMessageError(context);
        }
        return true;
    }

    @Override
    protected String[] getIgnoreForClients() {
        return this.getAnnotation().ignoreForClients();
    }

    private boolean buildMessageError(final ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(getAnnotation().message())
                .addConstraintViolation();
        return false;
    }
}
