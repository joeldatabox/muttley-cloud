package br.com.muttley.annotations.validators;


import br.com.muttley.exception.throwables.MuttleyException;
import lombok.Getter;
import org.apache.commons.logging.LogFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Joel Rodrigues Moreira on 20/03/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class MuttleyConstraintValidator<A extends Annotation, T> implements ConstraintValidator<A, T> {

    @Getter
    protected A annotation;

    @Override
    public final void initialize(final A annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        try {
            this.isValid(value);
            return true;
        } catch (MuttleyException ex) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(ex.getMessage())
                    .addPropertyNode(ex.getField())
                    .addConstraintViolation();
            return false;
        } catch (IllegalAccessException e) {
            LogFactory.getLog(this.getClass())
                    .error("Accessor method is not available for class : " + value.getClass().getName() + ", exception : " + e.getClass().getName(), e);
            throw new MuttleyException(e);
        } catch (InvocationTargetException e) {
            LogFactory.getLog(this.getClass())
                    .error("Field or method is not present on class : " + value.getClass().getName() + ", exception : : " + e.getClass().getName(), e);
            throw new MuttleyException(e);
        } catch (NoSuchMethodException e) {
            LogFactory.getLog(this.getClass())
                    .error("An exception occurred while accessing class : " + value.getClass().getName() + ", exception : : " + e.getClass().getName(), e);
            throw new MuttleyException(e);
        }
    }

    public abstract void isValid(T value) throws MuttleyException, IllegalAccessException, InvocationTargetException, NoSuchMethodException;
}
