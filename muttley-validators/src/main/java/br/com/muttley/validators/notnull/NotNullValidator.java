package br.com.muttley.validators.notnull;

import br.com.muttley.validators.MuttleyValidator;

import javax.validation.ConstraintValidatorContext;

/**
 * @author Joel Rodrigues Moreira on 04/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class NotNullValidator extends MuttleyValidator<NotNull, Object> {
    @Override
    protected boolean isValidValue(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(getAnnotation().message())
                    .addConstraintViolation();
            return false;
        }
        return true;
    }

    @Override
    protected String[] getIgnoreForClients() {
        return this.getAnnotation().ignoreForClients();
    }
}
