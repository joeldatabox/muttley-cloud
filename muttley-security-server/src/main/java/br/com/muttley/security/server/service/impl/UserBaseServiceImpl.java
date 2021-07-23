package br.com.muttley.security.server.service.impl;

import br.com.muttley.model.security.UserBase;
import br.com.muttley.security.server.repository.UserBaseRepository;
import br.com.muttley.security.server.service.UserBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import static br.com.muttley.model.security.Role.ROLE_USER_BASE_CREATE;

/**
 * @author Joel Rodrigues Moreira on 22/07/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class UserBaseServiceImpl extends SecurityServiceImpl<UserBase> implements UserBaseService {
    private static final String[] basicRoles = new String[]{ROLE_USER_BASE_CREATE.getSimpleName()};

    @Autowired
    public UserBaseServiceImpl(final UserBaseRepository repository, final MongoTemplate template) {
        super(repository, template, UserBase.class);
    }

    @Override
    public String[] getBasicRoles() {
        return basicRoles;
    }
}
