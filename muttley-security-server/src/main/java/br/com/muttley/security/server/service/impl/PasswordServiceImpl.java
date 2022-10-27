package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.service.Validator;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.headers.services.MetadataService;
import br.com.muttley.model.security.PasswdPayload;
import br.com.muttley.model.security.Password;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.UserResolverFromJWTEvent;
import br.com.muttley.security.server.repository.PasswordRepository;
import br.com.muttley.security.server.service.PasswordService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author Joel Rodrigues Moreira 12/03/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class PasswordServiceImpl<T extends Password> implements PasswordService<T> {
    private final PasswordRepository repository;
    private final MetadataService metadataService;
    private final Validator validator;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public PasswordServiceImpl(final PasswordRepository repository, final MetadataService metadataService, final Validator validator, final ApplicationEventPublisher publisher) {
        this.repository = repository;
        this.metadataService = metadataService;
        this.validator = validator;
        this.publisher = publisher;
    }

    @Override
    public Password findByUserId(final String userId) {
        return this.findByUser(new User().setId(userId));
    }

    private void checkPrecondictionSave(final User user, final T password) {
        //verificando se já tem uma senha salva pra determinado usuário
        if (this.repository.exists("user.$id", new ObjectId(password.getUser().getId()))) {
            throw new MuttleyBadRequestException(Password.class, "user", "Esse usuário já possuiu uma senha cadastrada");
        }
    }

    @Override
    public void save(final User user, final T password) {
        //verificando se realmente está criando um novo registro
        checkIdForSave(password);
        //garantindo que o metadata ta preenchido
        this.metadataService.generateNewMetadataFor(user, password);
        //processa regra de negocio antes de qualquer validação
        //this.beforeSave(user, value);
        //verificando precondições
        this.checkPrecondictionSave(user, password);
        if (password.getLastDatePasswordChanges() == null) {
            password.setLastDatePasswordChanges(new Date());
        }
        //validando dados do objeto
        this.validator.validate(password);

        this.repository.save(password);
        //realizando regras de enegocio depois do objeto ter sido salvo
        //this.afterSave(user, salvedValue);
        //valor salvo
    }

    @Override
    public void createPasswordFor(final User user, final String password) {
        final Password newPassword = Password.Builder
                .newInstance()
                .setUser(user)
                .setPassword(password)
                .builder();
        //verificando se realmente está criando um novo registro
        checkIdForSave((T) newPassword);
        //garantindo que o metadata ta preenchido
        this.metadataService.generateNewMetadataFor(user, newPassword);
        //processa regra de negocio antes de qualquer validação
        //this.beforeSave(user, value);
        //verificando precondições
        this.checkPrecondictionSave(user, (T) newPassword);
        //validando dados do objeto
        this.validator.validate(newPassword);

        this.repository.save(newPassword);
        //realizando regras de enegocio depois do objeto ter sido salvo
        //this.afterSave(user, salvedValue);
        //valor salvo
    }

    @Override
    public void createPasswordFor(final User user, final PasswdPayload password) {
        this.createPasswordFor(user, password.getNewPassword());
    }

    @Override
    public void update(final PasswdPayload password) {
        //recuperando o usuário necessário
        final UserResolverFromJWTEvent eventResolver = new UserResolverFromJWTEvent(password.getToken());
        this.publisher.publishEvent(eventResolver);
        final User user = eventResolver.getUserResolved();
        //recuperando a senha atual
        final Password currentPassword = this.findByUser(user);
        //realizando atualização
        currentPassword.setPassword(password);
        //salvando registros
        repository.save(currentPassword);
    }

    @Override
    public void resetePasswordFor(final User user, String password) {
        final Password passwordModel = this.findByUser(user);
        passwordModel.setPassword(password);
        repository.save(passwordModel);
    }

    private void checkIdForSave(final T value) {
        if (StringUtils.isEmpty(value.getId())) {
            value.setId(null);
        }
        if (value.getId() != null) {
            throw new MuttleyBadRequestException(value.getClass(), "id", "Não é possível criar um registro com um id existente");
        }
    }

    private Password findByUser(final User user) {
        final Password password = this.repository.findByUser(user);
        if (password == null) {
            throw new MuttleyBadRequestException(Password.class, "user", "Registro não encontrador");
        }
        return password;
    }

}
