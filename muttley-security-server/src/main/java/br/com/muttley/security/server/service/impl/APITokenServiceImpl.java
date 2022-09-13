package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.model.security.APIToken;
import br.com.muttley.model.security.User;
import br.com.muttley.security.server.repository.APITokenRepository;
import br.com.muttley.security.server.service.APITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author Joel Rodrigues Moreira on 09/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class APITokenServiceImpl extends SecurityServiceImpl<APIToken> implements APITokenService {
    private RSAPairKey

    @Autowired
    public APITokenServiceImpl(APITokenRepository repository, MongoTemplate mongoTemplate) {
        super(repository, mongoTemplate, APIToken.class);
    }

    @Override
    public void beforeSave(User user, APIToken value) {
        //gerando token de acesso
        value.setToken()
    }

    @Override
    public void checkPrecondictionUpdate(User user, APIToken value) {
        throw new MuttleyBadRequestException(APIToken.class, "", "Não é permitido fazer alteração de um token");
    }

    @Override
    public void checkPrecondictionUpdate(User user, Collection<APIToken> values) {
        throw new MuttleyBadRequestException(APIToken.class, "", "Não é permitido fazer alteração de um token");
    }
}
