package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;
import br.com.muttley.security.server.service.UserBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import static br.com.muttley.model.security.Role.ROLE_USER_BASE_CREATE;

/**
 * @author Joel Rodrigues Moreira on 26/11/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class UserBaseServiceImpl extends SecurityModelServiceImpl<UserBase> implements UserBaseService {
    private static final String[] basicRoles = new String[]{ROLE_USER_BASE_CREATE.getSimpleName()};
    private final String ODIN_USER;

    @Autowired
    public UserBaseServiceImpl(final MongoTemplate template, @Value("${muttley.security.odin.user}") final String odinUser) {
        super(template, UserBase.class);
        this.ODIN_USER = odinUser;
    }

    @Override
    public String[] getBasicRoles() {
        return basicRoles;
    }

    @Override
    public void checkPrecondictionSave(final User user, final UserBase value) {
        if (this.count(user, null) == 1) {
            throw new MuttleyBadRequestException(UserBase.class, null, "Já existe uma base de usuário cadastrada no sistema");
        }
    }

    @Override
    public void checkPrecondictionSave(final User user, final Owner owner, final UserBase value) {
        if (this.count(user, owner, null) == 1) {
            throw new MuttleyBadRequestException(UserBase.class, null, "Já existe uma base de usuário cadastrada no sistema");
        }
    }

    @Override
    public void checkPrecondictionDelete(final User user, final String id) {
        throw new MuttleyBadRequestException(Owner.class, "id", "Não é possível deletar a base de usuário");
    }
}
