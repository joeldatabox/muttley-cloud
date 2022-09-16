package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.XAPIToken;
import br.com.muttley.model.security.User;
import br.com.muttley.security.server.components.RSAPairKeyComponent;
import br.com.muttley.security.server.repository.APITokenRepository;
import br.com.muttley.security.server.service.APITokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;

import static br.com.muttley.model.security.rsa.RSAUtil.generateRandomString;

/**
 * @author Joel Rodrigues Moreira on 09/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class APITokenServiceImpl extends SecurityServiceImpl<XAPIToken> implements APITokenService {
    private RSAPairKeyComponent rsaPairKeyComponent;
    private final APITokenRepository repository;

    @Autowired
    public APITokenServiceImpl(APITokenRepository repository, MongoTemplate mongoTemplate) {
        super(repository, mongoTemplate, XAPIToken.class);
        this.repository = repository;
    }

    @Override
    public void beforeSave(User user, XAPIToken value) {
        //setando data de criação
        value.setDtCreate(new Date())
                .setLocaSeed(generateRandomString(15))
                //gerando token de acesso
                .setToken(rsaPairKeyComponent.encryptMessage(value.generateSeedHash()));
    }

    @Override
    public void checkPrecondictionUpdate(User user, XAPIToken value) {
        throw new MuttleyBadRequestException(XAPIToken.class, "", "Não é permitido fazer alteração de um token");
    }

    @Override
    public void checkPrecondictionUpdate(User user, Collection<XAPIToken> values) {
        throw new MuttleyBadRequestException(XAPIToken.class, "", "Não é permitido fazer alteração de um token");
    }

    @Override
    public User loadUserByAPIToken(String token) {
        final XAPIToken XAPIToken = this.repository.findByToken(token);
        if (XAPIToken == null) {
            throw new MuttleyNotFoundException(XAPIToken.class, "token", "Token não identificado");
        }
        return XAPIToken.getUser();
    }

    @Override
    public void afterDelete(User user, String id) {
        super.afterDelete(user, id);

    }

    @Override
    public void afterDelete(User user, XAPIToken value) {
        super.afterDelete(user, value);
    }
}
