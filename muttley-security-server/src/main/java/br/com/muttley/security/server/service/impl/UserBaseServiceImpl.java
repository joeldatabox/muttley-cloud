package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;
import br.com.muttley.model.security.UserPayLoad;
import br.com.muttley.model.security.UserView;
import br.com.muttley.security.server.service.UserBaseService;
import br.com.muttley.security.server.service.UserService;
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

    @Autowired
    public UserBaseServiceImpl(final MongoTemplate template, @Value("${muttley.security.odin.user}") final String odinUser, final UserService userService) {
        super(template, UserBase.class);
        this.ODIN_USER = odinUser;
        this.userService = userService;
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
    public void addUserItem(final User user, final User userForAdd) {
        if (!this.userHasBeenIncluded(user, userForAdd)) {
            this.mongoTemplate.updateFirst(
                    new Query(
                            where("owner.$id").is(user.getCurrentOwner().getObjectId())
                    ),
                    new Update()
                            .push("users")
                            .atPosition(FIRST)
                            .each(new UserItemForAdd().setAddedBy(user).setUser(userForAdd).setDtCreate(new Date()).setStatus(true)),
                    UserBase.class
            );
        }
    }

    @Override
    public void createNewUserAndAdd(final User user, final UserPayLoad payLoad) {
        final User salvedUser = userService.save(new User(payLoad));
        this.addUserItem(user, salvedUser);
    }

    /**
     * Verifica se o usuário já existe na base
     */
    private boolean userHasBeenIncluded(final User user, final User userForCheck) {
        return this.mongoTemplate.exists(
                new Query(
                        where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                .and("users.user.$id").is(new ObjectId(userForCheck.getId()))
                ), User.class
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
