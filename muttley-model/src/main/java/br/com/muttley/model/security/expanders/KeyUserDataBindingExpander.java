package br.com.muttley.model.security.expanders;

import br.com.muttley.model.security.KeyUserDataBinding;
import feign.Param;

/**
 * @author Joel Rodrigues Moreira 12/02/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class KeyUserDataBindingExpander implements Param.Expander {
    @Override
    public String expand(final Object key) {
        if (key == null) {
            return null;
        }
        return ((KeyUserDataBinding) key).getKey();
    }
}
