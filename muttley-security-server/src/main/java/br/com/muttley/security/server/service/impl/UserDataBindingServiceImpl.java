package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyConflictException;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserDataBinding;
import br.com.muttley.security.server.service.UserDataBindingService;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.muttley.model.security.Role.ROLE_USER_DATA_BINDING_READ;

/**
 * @author Joel Rodrigues Moreira 12/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class UserDataBindingServiceImpl extends SecurityModelServiceImpl<UserDataBinding> implements UserDataBindingService {
    private static final String[] basicRoles = new String[]{ROLE_USER_DATA_BINDING_READ.getSimpleName()};

    public UserDataBindingServiceImpl(final MongoTemplate mongoTemplate) {
        super(mongoTemplate, UserDataBinding.class);
    }

    @Override
    public String[] getBasicRoles() {
        return basicRoles;
    }

    @Override
    public void beforeSave(final User user, final UserDataBinding value) {
        if (value.getUser() == null) {
            value.setUser(user);
        }
    }

    @Override
    public void checkPrecondictionSave(final User user, final UserDataBinding value) {
        if (!user.equals(value.getUser())) {
            throw new MuttleyBadRequestException(UserDataBinding.class, "user", "O usuário informado é diferente do da requisição!");
        }
        //verificando se já não existe um registro com as informações
        if (this.repository.exists(user.getCurrentOwner(), "user.$id", new ObjectId(value.getUser().getId()), "key", value.getKey())) {
            throw new MuttleyConflictException(UserDataBinding.class, "key", "Jás existe um registro com essas informações");
        }

    }

    @Override
    public List<UserDataBinding> listByUserName(final User user, final String userName) {
        if (user.getUserName().equals(userName)) {
            return this.findAll(user, null);
        }
        return null;
    }

    @Override
    public UserDataBinding saveByUserName(final User user, final String userName, final UserDataBinding value) {
        return null;
    }

    @Override
    public UserDataBinding updateByUserName(final User user, final String userName, final UserDataBinding value) {
        return null;
    }
}
