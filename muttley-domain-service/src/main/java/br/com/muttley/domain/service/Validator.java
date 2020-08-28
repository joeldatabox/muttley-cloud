package br.com.muttley.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collection;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Created by joel on 30/03/17.
 */
@Component
public class Validator {

    private final SpringValidatorAdapter validator;

    @Autowired
    public Validator(final javax.validation.Validator validator) {
        this.validator = new SpringValidatorAdapter(validator);
    }

    public final void validate(final Object o) {
        final Set<ConstraintViolation<Object>> violations = validator.validate(o);
        if (!isEmpty(violations)) {
            throw new ConstraintViolationException("test", violations);
        }
    }

    public final void validateCollection(final Collection<?> value) {
        value.stream().forEach(this::validate);
    }
}
