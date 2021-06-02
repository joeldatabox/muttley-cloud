package br.com.muttley.validators;

import br.com.muttley.headers.components.MuttleyUserAgent;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

/**
 * @author Joel Rodrigues Moreira on 04/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
public abstract class MuttleyValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

    protected A annotation;

    @Autowired
    protected MuttleyUserAgent userAgent;

    @Override
    public void initialize(final A constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public final boolean isValid(final T value, final ConstraintValidatorContext context) {

        //é uma anotação que está em uma classe e não em um campo?
        /*if (isAnnotationForClass() || !(value instanceof Document)) {
            return true;
        }
*/

        //é para ignorar essa validação com base no userAgenteName?
        if (this.isIgnoreValidation(value, context)) {
            return true;
        }
        /*if (isAnnotationForClass() || !(value instanceof Document)) {
            return true;
        }
*/
        return this.isValidValue(value, context);

        /*//Devemos validar essa info nessa requisição?
        if (!this.isIgnoreValidation(value, context)) {
            //é uma anotação que está em uma classe e não em um campo?
            if (isAnnotationForClass() || !(value instanceof Document)) {
                return true;
            }
        }
        return isValidValue(value, context);*/
    }

    /**
     * Informa se deve ingnorar ou não a validação durante a requisição
     */
    protected boolean isIgnoreValidation(Object value, ConstraintValidatorContext context) {
        if (!this.isEmpty(this.getIgnoreForClients()) && this.getUserAgent().containsValidValue()) {
            for (final String client : this.getIgnoreForClients()) {
                if (this.getUserAgent().getCurrentValue().equals(client)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected abstract boolean isValidValue(final T value, final ConstraintValidatorContext context);

    /**
     * Informa se é uma anotação de classe ou não
     */
    protected boolean isAnnotationForClass() {
        return false;
    }

    private boolean isEmpty(final String[] values) {
        return values == null || values.length == 0;
    }

    protected abstract String[] getIgnoreForClients();
}
