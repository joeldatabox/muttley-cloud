package br.com.muttley.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

/**
 * Created by joel on 30/03/17.
 */
@Component
public class Validator {

    private final javax.validation.Validator validator;

    @Autowired
    public Validator(final javax.validation.Validator validator) {
        this.validator = validator;
    }

    public final void validate(final Object o) {
        final Set<ConstraintViolation<Object>> violations = validator.validate(o);
        if (violations != null && !violations.isEmpty()) {
            throw new ConstraintViolationException("test", violations);
        }
    }
}
