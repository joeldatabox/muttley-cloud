package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.headers.components.MuttleyCurrentVersion;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.XAPIToken;
import br.com.muttley.security.server.components.RSAPairKeyComponent;
import br.com.muttley.security.server.repository.XAPITokenRepository;
import br.com.muttley.security.server.service.XAPITokenService;
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
public class XAPITokenServiceImpl extends SecurityServiceImpl<XAPIToken> implements XAPITokenService {
    private RSAPairKeyComponent rsaPairKeyComponent;
    private final XAPITokenRepository repository;

    private final MuttleyCurrentVersion currentVersion;

    @Autowired
    public XAPITokenServiceImpl(XAPITokenRepository repository, MongoTemplate mongoTemplate, MuttleyCurrentVersion currentVersion) {
        super(repository, mongoTemplate, XAPIToken.class);
        this.repository = repository;
        this.currentVersion = currentVersion;
    }

    @Override
    public void beforeSave(User user, XAPIToken value) {
        //setando data de criação
        value.setDtCreate(new Date())
                .setLocaSeed(generateRandomString(15))
                //gerando token de acesso
                .setToken(this.rsaPairKeyComponent.encryptMessage(value.generateSeedHash()));
    }

    @Override
    public void checkPrecondictionSave(User user, XAPIToken value) {
        super.checkPrecondictionSave(user, value);
        //verificando se o conteudo bate com a seed
        if (!this.rsaPairKeyComponent.decryptMessage(value.getToken()).equals(value.generateSeedHash())) {
            throw new MuttleyBadRequestException(XAPIToken.class, "token", "Token inválido!");
        }
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
    public XAPIToken generateXAPIToken(final User user) {
        return this.save(user,
                new XAPIToken()
                        .setUser(user)
                        .setOwner(user.getCurrentOwner())
                        .setVersion(this.currentVersion.getCurrenteFromServer())
        );
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
