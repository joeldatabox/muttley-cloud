package br.com.muttley.validators.checkIndex;

import br.com.muttley.exception.throwables.MuttleyConflictException;
import br.com.muttley.validators.MuttleyConstraintValidator;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.validation.ConstraintValidator;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Joel Rodrigues Moreira on 12/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

public class CheckIndexValidator extends MuttleyConstraintValidator<CheckIndex, Object> implements ConstraintValidator<CheckIndex, Object> {

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void isValid(final Object value) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        final Criteria criteria = new Criteria();
        for (String field : this.annotation.fields()) {
            criteria.and(field).is(PropertyUtils.getProperty(value, field));
        }
        if (mongoOperations.exists(new Query(criteria), value.getClass())) {
            throw new MuttleyConflictException(value.getClass(), this.annotation.fieldOwner(), this.annotation.message());
        }
    }


}
