package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.service.Validator;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;
import br.com.muttley.model.security.UserBaseItem;
import br.com.muttley.model.security.UserData;
import br.com.muttley.model.security.UserDataBinding;
import br.com.muttley.model.security.UserView;
import br.com.muttley.model.security.merge.MergedUserBaseItemResponse;
import br.com.muttley.model.security.merge.MergedUserBaseResponse;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import br.com.muttley.security.server.events.NewUserHasBeenAddedInBaseEvent;
import br.com.muttley.security.server.service.PassaportService;
import br.com.muttley.security.server.service.UserBaseService;
import br.com.muttley.security.server.service.UserDataBindingService;
import br.com.muttley.security.server.service.UserService;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.hibernate.validator.internal.engine.ConstraintViolationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolationException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static br.com.muttley.model.security.Role.ROLE_USER_BASE_CREATE;
import static br.com.muttley.model.security.merge.Status.CONFLICT;
import static br.com.muttley.model.security.merge.Status.ERROR;
import static br.com.muttley.model.security.merge.Status.OK;
import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
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
    private final DocumentNameConfig documentNameConfig;
    private final PassaportService passaportService;
    private final Validator validator;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public UserBaseServiceImpl(
            final MongoTemplate template,
            @Value("${muttley.security.odin.user}") final String odinUser,
            final UserService userService,
            final UserDataBindingService dataBindingService,
            final DocumentNameConfig documentNameConfig,
            final PassaportService passaportService,
            final Validator validator,
            final ApplicationEventPublisher publisher) {
        super(template, UserBase.class);
        this.ODIN_USER = odinUser;
        this.userService = userService;
        this.dataBindingService = dataBindingService;
        this.documentNameConfig = documentNameConfig;
        this.passaportService = passaportService;
        this.validator = validator;
        this.publisher = publisher;
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
    public void checkPrecondictionSave(final User user, final OwnerData owner, final UserBase value) {
        if (this.count(user, owner, null) == 1) {
            throw new MuttleyBadRequestException(UserBase.class, null, "Já existe uma base de usuário cadastrada no sistema");
        }
    }

    @Override
    public void checkPrecondictionDelete(final User user, final String id) {
        throw new MuttleyBadRequestException(Owner.class, "id", "Não é possível deletar a base de usuário");
    }

    @Override
    public boolean userNameIsAvaliableForUserName(final User user, final String userName, final Set<String> userNames) {
        return this.userService.userNameIsAvaliableForUserName(userName, userNames);
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
        this.addUserItemIfNotExists(user, new UserBaseItem(user, userForAdd, null, new Date(), true, null));
    }

    @Override
    public void addUserItemIfNotExists(final User user, final UserBaseItem userForAdd) {
        if (!this.hasBeenIncludedAnyGroup(user, userForAdd.getUser())) {
            userForAdd.setAddedBy(user);
            if (userForAdd.getDtCreate() == null) {
                userForAdd.setDtCreate(new Date());
            }
            if (userForAdd.getAddedBy() == null) {
                userForAdd.setAddedBy(user);
            }
            this.validator.validate(userForAdd);
            if (this.hasBeenIncludedAnyGroup(user, userForAdd.getUser())) {
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
        } else {
            this.mongoTemplate.updateMulti(
                    new Query(
                            where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                    .and("users.user.$id").is(new ObjectId(userForAdd.getUser().getId()))
                    ),
                    new Update().set("users.$.status", userForAdd.isStatus()),
                    UserBase.class
            );
        }

        this.publisher.publishEvent(new NewUserHasBeenAddedInBaseEvent(new NewUserHasBeenAddedInBaseEvent.NewUserHasBeenAddedInBaseItemEvent(user, userForAdd.getUser())));
    }

    @Override
    public void createNewUserAndAdd(final User user, final UserBaseItem item) {
        //final User userForSave = new User(item.getUserInfoForMerge());
        if (!item.dataBindingsIsEmpty()) {
            item.getDataBindings().forEach(it -> {
                if (it.getKey().isUnique()) {
                    if (this.dataBindingService.containsByKeyAndValueAndUserNameNotEq(user, item.getUserInfoForMerge().getUserName(), it.getKey(), it.getValue())) {
                        throw new MuttleyBadRequestException(UserDataBinding.class, "key", "Já existe um usuário que possui ligação com " + it.getKey().getDisplayKey() + " informado(a)");
                    }
                }
            });
        }
        final User salvedUser = userService.save(item.getUserInfoForMerge());
        if (!item.dataBindingsIsEmpty()) {
            this.dataBindingService.merge(user, salvedUser.getUserName(), item.getDataBindings());
        }
        this.addUserItemIfNotExists(user, salvedUser);
    }

    @Override
    public void mergeUserItemIfExists(final User user, final UserBaseItem item) {
        item.setUser(userService.update(user, new User(item.getUserInfoForMerge())));
        if (!item.dataBindingsIsEmpty()) {
            this.dataBindingService.merge(user, item.getUser().getUserName(), item.getDataBindings());
        }
        this.addUserItemIfNotExists(user, item);
    }

    @Override
    public void mergeUserItem(User user, UserBaseItem item) {
        if (this.userService.existUserByEmailOrUserNameOrNickUsers(item.getUserInfoForMerge().getEmail(), item.getUserInfoForMerge().getUserName(), item.getUserInfoForMerge().getNickUsers())) {
            this.mergeUserItemIfExists(user, item);
        } else {
            this.createNewUserAndAdd(user, item);
        }

    }

    @Override
    public MergedUserBaseResponse mergeUserItens(final User user, final List<UserBaseItem> itens) {
        final MergedUserBaseResponse response = new MergedUserBaseResponse();
        itens.stream().map(UserBaseItem::getUserInfoForMerge).forEach(item -> {
            final MergedUserBaseItemResponse it = new MergedUserBaseItemResponse(item.getEmail(), OK);
            if (item == null || item.getEmail() == null) {
                it.setStatus(ERROR);
                it.addDetails("email", "Informe um email válidp");
            }

            response.add(it);
            //validando item por si só
            try {
                this.validator.validate(item);

                //verificando se o username informado está disponível
                if (!StringUtils.isEmpty(item.getUserName()) && !this.userService.userNameIsAvaliable(item.getUserName())) {
                    it.setStatus(CONFLICT);
                    it.addDetails("userName", "Esse userName não está disponível");
                }
                //verificando o email
                if (!StringUtils.isEmpty(item.getEmail()) && !this.userService.userNameIsAvaliable(item.getEmail())) {
                    it.setStatus(CONFLICT);
                    it.addDetails("email", "Esse email não está disponível");
                }
                //verificando os nickUsers
                if (item.getNickUsers() != null) {
                    item.getNickUsers().forEach(nick -> {
                        if (!StringUtils.isEmpty(nick) && !this.userService.userNameIsAvaliable(nick)) {
                            it.setStatus(CONFLICT);
                            it.addDetails("nickUsers[" + nick + "]", "Esse nickUser não está disponível");
                        }
                    });
                }
            } catch (final ConstraintViolationException ex) {
                final ConstraintViolationImpl violation = ((ConstraintViolationImpl) ex.getConstraintViolations().toArray()[0]);
                it.setStatus(ERROR)
                        .addDetails(violation.getPropertyPath().toString(), violation.getMessage());
            }
        });
        response.getItens()
                .stream()
                .filter(it -> OK.equals(it.getStatus()))
                .forEach(it -> {
                    this.mergeUserItem(user,
                            itens.stream().filter(item -> it.getEmail().equals(item.getUserInfoForMerge().getEmail()))
                                    .findFirst()
                                    .get()
                    );
                });

        return response;
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
        this.passaportService.removeUserFromAllPassaport(user.getCurrentOwner(), userLoaded);
    }

    @Override
    public boolean hasBeenIncludedAnyGroup(final User user, final UserData userForCheck) {
        return this.mongoTemplate.exists(
                new Query(
                        where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                .and("users.user.$id").is(new ObjectId(userForCheck.getId()))
                ), UserBase.class
        );
    }

    @Override
    public boolean hasBeenIncludedAnyGroup(final UserData userForCheck) {
        return this.mongoTemplate.exists(
                new Query(
                        /*where("owner.$id").is(user.getCurrentOwner().getObjectId())*/
                        where("users.user.$id").is(new ObjectId(userForCheck.getId()))
                ), UserBase.class
        );
    }

    @Override
    public boolean hasBeenIncludedAnyGroup(final User user, final String userNameForCheck) {
        /**
         * db.getCollection("muttley-users-base").aggregate([
         *     {$match:{"owner.$id":ObjectId("5e28b3e3637e580001e465d6")}},
         *     {$unwind:"$users"},
         *     {$project:{user:{$objectToArray:"$users.user"}}},
         *     {$project:{user:{$arrayElemAt:["$user.v",1]}}},
         *     {$lookup:{
         *         from:"muttley-users",
         *         localField:"user",
         *         foreignField:"_id",
         *         as:"user"
         *     }},
         *     {$unwind:"$user"},
         *     {$match:{
         *         $or:[
         *             {"user.userName":"asdfasd234asdfasdf"},
         *             {"user.email":"df7899@gmail.com"},
         *             {"user.nickUsers":{$in:["asdfas"]}},
         *         ]
         *     }},
         *     {$group:{_id:"$user"}},
         *     {$count:"result"}
         * ])
         */
        final AggregationResults<UserViewServiceImpl.ResultCount> results = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId())),
                        unwind("$users"),
                        project().and(context -> new BasicDBObject("$objectToArray", "$users.user")).as("user"),
                        project().and(context -> new BasicDBObject("$arrayElemAt", asList("$user.v", 1))).as("user"),
                        lookup(
                                this.documentNameConfig.getNameCollectionUser(),
                                "user",
                                "_id",
                                "user"
                        ),
                        unwind("$user"),
                        match(new Criteria().orOperator(
                                where("user.userName").is(userNameForCheck),
                                where("user.email").is(userNameForCheck),
                                where("user.nickUsers").in(asList(userNameForCheck))
                        )),
                        group("$user"),
                        Aggregation.count().as("count")
                ),
                this.documentNameConfig.getNameCollectionUserBase(),
                UserViewServiceImpl.ResultCount.class
        );
        if (results == null || results.getUniqueMappedResult() == null || results.getUniqueMappedResult().getCount() == 0) {
            return false;
        }
        if (results.getUniqueMappedResult().getCount() > 1) {
            //não pode retornar mais de um usuário
            throw new MuttleyException("Erro interno");
        }

        return true;
    }

    @Override
    public boolean hasBeenIncludedAnyGroup(final String userNameForCheck) {
        /**
         * db.getCollection("muttley-users-base").aggregate([
         *     {$unwind:"$users"},
         *     {$project:{user:{$objectToArray:"$users.user"}}},
         *     {$project:{user:{$arrayElemAt:["$user.v",1]}}},
         *     {$lookup:{
         *         from:"muttley-users",
         *         localField:"user",
         *         foreignField:"_id",
         *         as:"user"
         *     }},
         *     {$unwind:"$user"},
         *     {$match:{
         *         $or:[
         *             {"user.userName":"asdfasd234asdfasdf"},
         *             {"user.email":"df7899@gmail.com"},
         *             {"user.nickUsers":{$in:["asdfas"]}},
         *         ]
         *     }},
         *     {$group:{_id:"$user"}},
         *     {$count:"result"}
         * ])
         */
        final AggregationResults<UserViewServiceImpl.ResultCount> results = this.mongoTemplate.aggregate(
                newAggregation(
                        unwind("$users"),
                        project().and(context -> new BasicDBObject("$objectToArray", "$users.user")).as("user"),
                        project().and(context -> new BasicDBObject("$arrayElemAt", asList("$user.v", 1))).as("user"),
                        lookup(
                                this.documentNameConfig.getNameCollectionUser(),
                                "user",
                                "_id",
                                "user"
                        ),
                        unwind("$user"),
                        match(new Criteria().orOperator(
                                where("user.userName").is(userNameForCheck),
                                where("user.email").is(userNameForCheck),
                                where("user.nickUsers").in(asList(userNameForCheck))
                        )),
                        group("$user"),
                        Aggregation.count().as("count")
                ),
                UserBase.class,
                UserViewServiceImpl.ResultCount.class
        );
        if (results == null || results.getUniqueMappedResult() == null || results.getUniqueMappedResult().getCount() == 0) {
            return false;
        }
        if (results.getUniqueMappedResult().getCount() > 1) {
            //não pode retornar mais de um usuário
            throw new MuttleyException("Erro interno");
        }

        return true;
    }

    @Override
    public boolean allHasBeenIncludedGroup(User user, Collection<User> users) {
        return this.mongoTemplate.exists(
                new Query(
                        where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                .andOperator(
                                        users.parallelStream()
                                                .map(it -> where("users.user.$id").is(it.getObjectId()))
                                                .toArray(Criteria[]::new)
                                )
                ), UserBase.class
        );
    }
}
