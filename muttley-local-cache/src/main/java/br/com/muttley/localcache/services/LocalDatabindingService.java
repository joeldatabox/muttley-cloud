package br.com.muttley.localcache.services;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserDataBinding;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira 24/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface LocalDatabindingService {
    public static final String BASIC_KEY = "USER-DATABINDING:";

    List<UserDataBinding> getUserDataBindings(final JwtToken jwtUser, final User user);

    void expireUserDataBindings(final User user);
}
