package br.com.muttley.annotations.valitators;

import br.com.muttley.exception.throwables.MuttleyConflictException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Joel Rodrigues Moreira on 12/06/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */

public class CheckIndexValidator implements ConstraintValidator<CheckIndex, Object> {
    private CheckIndex checkIndex;
    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void initialize(final CheckIndex checkIndex) {
        this.checkIndex = checkIndex;
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            final Criteria criteria = new Criteria();
            for (String field : this.checkIndex.fields()) {
                criteria.and(field).is(PropertyUtils.getProperty(value, field));
            }
            if (mongoOperations.exists(new Query(criteria), value.getClass())) {
                throw new MuttleyConflictException(value.getClass(), "", checkIndex.message());
            }

            //final String cpfCnpj = (String) PropertyUtils.getProperty(value, this.checkIndex.fields());
            return true;
        } catch (IllegalAccessException e) {
            LogFactory.getLog(CheckIndex.class).error("Accessor method is not available for class : " + value.getClass().getName() + ", exception : " + e.getClass().getName(), e);
            return false;
        } catch (InvocationTargetException e) {
            LogFactory.getLog(CheckIndex.class).error("Field or method is not present on class : " + value.getClass().getName() + ", exception : : " + e.getClass().getName(), e);
            return false;
        } catch (NoSuchMethodException e) {
            LogFactory.getLog(CheckIndex.class).error("An exception occurred while accessing class : " + value.getClass().getName() + ", exception : : " + e.getClass().getName(), e);
            return false;
        }
    }


}
