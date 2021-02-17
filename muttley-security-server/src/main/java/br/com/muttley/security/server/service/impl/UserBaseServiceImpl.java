package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;
import br.com.muttley.model.security.UserBaseItem;
import br.com.muttley.model.security.UserPayLoad;
import br.com.muttley.model.security.UserView;
import br.com.muttley.security.server.service.UserBaseService;
import br.com.muttley.security.server.service.UserDataBindingService;
import br.com.muttley.security.server.service.UserService;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

import static br.com.muttley.model.security.Role.ROLE_USER_BASE_CREATE;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Update.Position.FIRST;

/**
 * @author Joel Rodrigues Moreira on 26/11/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class UserBaseServiceImpl extends SecurityModelServiceImpl<UserBase> implements UserBaseService {
    private static final String[] basicRoles = new String[]{ROLE_USER_BASE_CREATE.getSimpleName()};
    private final String ODIN_USER;
    private final UserService userService;
    private final UserDataBindingService dataBindingService;

    @Autowired
    public UserBaseServiceImpl(
            final MongoTemplate template,
            @Value("${muttley.security.odin.user}") final String odinUser,
            final UserService userService,
            final UserDataBindingService dataBindingService) {
        super(template, UserBase.class);
        this.ODIN_USER = odinUser;
        this.userService = userService;
        this.dataBindingService = dataBindingService;
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

    @Override
    public boolean userNameIsAvaliable(final User user, final Set<String> userNames) {
        return this.userService.userNameIsAvaliable(userNames);
    }

    @Override
    public UserView findUserByEmailOrUserNameOrNickUser(final User user, final String emailOrUserName) {
        return new UserView(this.userService.findUserByEmailOrUserNameOrNickUser(emailOrUserName));
    }

    @Override
    public void addUserItemIfNotExists(final User user, final User userForAdd) {
        this.addUserItemIfNotExists(user, new UserBaseItem(user, userForAdd, new Date(), true, null));
    }

    @Override
    public void addUserItemIfNotExists(final User user, final UserBaseItem userForAdd) {
        if (!this.userHasBeenIncluded(user, userForAdd.getUser().getId())) {
            userForAdd.setAddedBy(user);
            if (userForAdd.getDtCreate() == null) {
                userForAdd.setDtCreate(new Date());
            }
            if (userForAdd.getAddedBy() == null) {
                userForAdd.setAddedBy(user);
            }
            this.validator.validate(userForAdd);
            if (this.userHasBeenIncluded(user, userForAdd.getUser().getId())) {
                throw new MuttleyBadRequestException(UserBase.class, "users", "Usuário já está presente na base");
            }
            this.mongoTemplate.updateFirst(
                    new Query(
                            where("owner.$id").is(user.getCurrentOwner().getObjectId())
                    ),
                    new Update()
                            .push("users")
                            .atPosition(FIRST)
                            .each(userForAdd),
                    UserBase.class
            );
        }
    }

    @Override
    public void createNewUserAndAdd(final User user, final UserPayLoad payLoad) {
        final User salvedUser = userService.save(new User(payLoad));
        if (!payLoad.dataBindingsIsEmpty()) {
            this.dataBindingService.merge(user, salvedUser.getUserName(), payLoad.getDataBindings());
        }
        this.addUserItemIfNotExists(user, salvedUser);
    }

    @Override
    public void mergeUserItemIfExists(final User user, final UserPayLoad payLoad) {
        final User salvedUser = userService.update(user, new User(payLoad));
        if (!payLoad.dataBindingsIsEmpty()) {
            this.dataBindingService.merge(user, salvedUser.getUserName(), payLoad.getDataBindings());
        }
        this.addUserItemIfNotExists(user, salvedUser);
    }

    @Override
    public void removeByUserName(final User user, final String userName) {
        final User userLoaded = this.userService.findByUserName(userName);
        this.mongoTemplate.updateFirst(
                new Query(
                        where("owner.$id").is(user.getCurrentOwner().getObjectId())
                ),
                new Update()
                        .pull("users", new BasicDBObject("user.$id", new ObjectId(userLoaded.getId()))),
                UserBase.class
        );
    }

    /**
     * Verifica se o usuário já existe na base
     */
    private boolean userHasBeenIncluded(final User user, final User userForCheck) {
        return this.userHasBeenIncluded(user, userForCheck.getId());
    }

    private boolean userHasBeenIncluded(final User user, final String id) {
        return this.mongoTemplate.exists(
                new Query(
                        where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                .and("users.user.$id").is(new ObjectId(id))
                ), UserBase.class
        );
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    private class UserItemForAdd {
        @DBRef
        @NotNull(message = "Informe o usuário que está efetuando essa operação")
        private User addedBy;

        @DBRef
        @NotNull(message = "Informe o usuário participante da base")
        private User user;

        @NotNull
        private Date dtCreate;

        private boolean status;
    }
}
