package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.APIToken;
import br.com.muttley.model.security.User;
import br.com.muttley.security.server.components.RSAPairKeyComponent;
import br.com.muttley.security.server.repository.APITokenRepository;
import br.com.muttley.security.server.service.APITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 09/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class APITokenServiceImpl extends SecurityServiceImpl<APIToken> implements APITokenService {
    private RSAPairKeyComponent rsaPairKeyComponent;
    private final APITokenRepository repository;

    @Autowired
    public APITokenServiceImpl(APITokenRepository repository, MongoTemplate mongoTemplate) {
        super(repository, mongoTemplate, APIToken.class);
        this.repository = repository;
    }

    @Override
    public void beforeSave(User user, APIToken value) {
        //setando data de criação
        value.setDtCreate(new Date());
        //gerando token de acesso
        value.setToken(rsaPairKeyComponent.encryptMessage(value.generateSeedHash()));
    }

    @Override
    public void checkPrecondictionUpdate(User user, APIToken value) {
        throw new MuttleyBadRequestException(APIToken.class, "", "Não é permitido fazer alteração de um token");
    }

    @Override
    public void checkPrecondictionUpdate(User user, Collection<APIToken> values) {
        throw new MuttleyBadRequestException(APIToken.class, "", "Não é permitido fazer alteração de um token");
    }

    @Override
    public User loadUserByAPIToken(String token) {
        final APIToken apiToken = this.repository.findByToken(token);
        if (apiToken == null) {
            throw new MuttleyNotFoundException(APIToken.class, "token", "Token não identificado");
        }
        return apiToken.getUser();
    }

    @Override
    public void afterDelete(User user, String id) {
        super.afterDelete(user, id);

    }

    @Override
    public void afterDelete(User user, APIToken value) {
        super.afterDelete(user, value);
    }
}
