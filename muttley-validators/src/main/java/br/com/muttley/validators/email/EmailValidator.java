package br.com.muttley.validators.email;

import br.com.muttley.validators.MuttleyValidator;

import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class EmailValidator extends MuttleyValidator<Email, String> {
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private final Pattern pattern;

    public EmailValidator() {
        this.pattern = Pattern.compile(EMAIL_PATTERN);
    }

    @Override
    protected boolean isValidValue(String email, ConstraintValidatorContext context) {
        if (email != null && !this.pattern.matcher(email).matches()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(getAnnotation().message())
                    .addConstraintViolation();
        }
        return true;
    }

    @Override
    protected String[] getIgnoreForClients() {
        return this.getAnnotation().ignoreForClients();
    }
}
