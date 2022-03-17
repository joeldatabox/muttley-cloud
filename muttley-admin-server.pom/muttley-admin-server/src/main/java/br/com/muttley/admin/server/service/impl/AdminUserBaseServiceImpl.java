package br.com.muttley.admin.server.service.impl;

import br.com.muttley.admin.server.config.model.DocumentNameConfig;
import br.com.muttley.admin.server.service.AdminPassaportService;
import br.com.muttley.admin.server.service.AdminUserBaseService;
import br.com.muttley.admin.server.service.AdminUserDataBindingService;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.model.BasicAggregateResultCount;
import br.com.muttley.model.admin.AdminOwner;
import br.com.muttley.model.admin.AdminUserBase;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;
import br.com.muttley.model.security.UserBaseItem;
import br.com.muttley.model.security.UserData;
import br.com.muttley.model.security.UserView;
import br.com.muttley.mongo.service.infra.AggregationUtils;
import br.com.muttley.mongo.service.infra.metadata.EntityMetaData;
import br.com.muttley.security.feign.UserServiceClient;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static br.com.muttley.model.security.Role.ROLE_USER_BASE_CREATE;
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
 * @author Joel Rodrigues Moreira on 27/04/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class AdminUserBaseServiceImpl extends AdminServiceImpl<AdminUserBase> implements AdminUserBaseService {
    private static final String[] basicRoles = new String[]{ROLE_USER_BASE_CREATE.getSimpleName()};
    private final UserServiceClient userService;
    //private final AdminUserDataBindingService dataBindingService;
    private final DocumentNameConfig documentNameConfig;
    private final AdminPassaportService passaportService;

    @Autowired
    public AdminUserBaseServiceImpl(
            final MongoTemplate template,
            final UserServiceClient userService,
            final AdminUserDataBindingService dataBindingService,
            final DocumentNameConfig documentNameConfig,
            final AdminPassaportService passaportService) {
        super(null, template, AdminUserBase.class);
        this.userService = userService;
        //  this.dataBindingService = dataBindingService;
        this.documentNameConfig = documentNameConfig;
        this.passaportService = passaportService;
    }

    @Override
    public String[] getBasicRoles() {
        return basicRoles;
    }

    @Override
    public void checkPrecondictionSave(final User user, final AdminUserBase value) {
        final AggregationResults result = this.mongoTemplate.aggregate(
                newAggregation(
                        AggregationUtils.createAggregationsCount(EntityMetaData.of(AdminUserBase.class), null,
                                addOwnerQueryParam(value.getOwner(), null)
                        )),
                this.clazz, ResultCount.class);
        if ((result.getUniqueMappedResult() != null ? ((ResultCount) result.getUniqueMappedResult()).getCount() : 0) == 1) {
            throw new MuttleyBadRequestException(UserBase.class, null, "Já existe uma base de usuário cadastrada no sistema");
        }
    }

    @Override
    public AdminUserBase save(final User user, final AdminUserBase value) {
        //verificando se realmente está criando um novo registro
        checkIdForSave(value);
        //garantindo que o metadata ta preenchido
        //this.createMetaData(user, value);
        this.metadataService.generateNewMetadataFor(user, value);
        //processa regra de negocio antes de qualquer validação
        this.beforeSave(user, value);
        //verificando precondições
        this.checkPrecondictionSave(user, value);
        //validando dados do objeto
        this.validator.validate(value);
        this.mongoTemplate.save(value, this.documentNameConfig.getNameCollectionAdminUserBase());
        final AdminUserBase otherValue = this.mongoTemplate.findOne(new Query(), AdminUserBase.class, this.documentNameConfig.getNameCollectionAdminUserBase());
        //realizando regras de enegocio depois do objeto ter sido salvo
        this.afterSave(user, otherValue);
        //valor salvo
        return otherValue;
    }

    //@Override
    public void checkPrecondictionSave(final User user, final OwnerData owner, final AdminUserBase value) {
        /*if (this.count(user, owner, null) == 1) {
            throw new MuttleyBadRequestException(UserBase.class, null, "Já existe uma base de usuário cadastrada no sistema");
        }*/
        if (owner != null) {
            throw new MuttleyException();
        }
        final AggregationResults result = this.mongoTemplate.aggregate(
                newAggregation(
                        AggregationUtils.createAggregationsCount(EntityMetaData.of(AdminUserBase.class), null,
                                addOwnerQueryParam(owner, null)
                        )),
                this.clazz, ResultCount.class);

        if ((result.getUniqueMappedResult() != null ? ((ResultCount) result.getUniqueMappedResult()).getCount() : 0) == 1) {
            throw new MuttleyBadRequestException(AdminUserBase.class, null, "Já existe uma base de usuário cadastrada no sistema");
        }
    }

    private final Map<String, String> addOwnerQueryParam(final OwnerData owner, final Map<String, String> queryParams) {
        final Map<String, String> query = new LinkedHashMap<>(1);
        query.put("owner.$id.$is", owner.getObjectId().toString());
        if (queryParams != null) {
            query.putAll(queryParams);
        }
        return query;
    }


    @Override
    public void checkPrecondictionDelete(final User user, final String id) {
        throw new MuttleyBadRequestException(Owner.class, "id", "Não é possível deletar a base de usuário");
    }

    @Override
    public AdminUserBase save(final User user, final OwnerData owner, final AdminUserBase userBase) {
        throw new NotImplementedException();
    }

    @Override
    public boolean userNameIsAvaliableForUserName(final User user, final String userName, final Set<String> userNames) {
        //return this.userService.userNameIsAvaliableForUserName(userName, userNames);
        throw new NotImplementedException();
    }

    @Override
    public boolean userNameIsAvaliable(final User user, final Set<String> userNames) {
        return this.userService.userNameIsAvaliable(userNames);
    }

    @Override
    public UserView findUserByEmailOrUserNameOrNickUser(final User user, final String emailOrUserName) {
        return new UserView(this.userService.findUserByEmailOrUserNameOrNickUsers(emailOrUserName, emailOrUserName, new HashSet<>(asList(emailOrUserName))));
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
    }

    @Override
    public void createNewUserAndAdd(final User user, final UserBaseItem item) {
        final User userForSave = new User(item.getUserInfoForMerge());
        /*if (!item.dataBindingsIsEmpty()) {
            item.getDataBindings().forEach(it -> {
                if (it.getKey().isUnique()) {
                    if (this.dataBindingService.containsByKeyAndValueAndUserNameNotEq(user, userForSave.getUserName(), it.getKey(), it.getValue())) {
                        throw new MuttleyBadRequestException(UserDataBinding.class, "key", "Já existe um usuário que possui ligação com " + it.getKey().getDisplayKey() + " informado(a)");
                    }
                }
            });
        }*/

        final User salvedUser = userService.save(item.getUserInfoForMerge(), "true");

        /*if (!item.dataBindingsIsEmpty()) {
            this.dataBindingService.merge(user, salvedUser.getUserName(), item.getDataBindings().parallelStream().map(AdminUserDataBinding::new).collect(Collectors.toSet()));
        }*/
        this.addUserItemIfNotExists(user, salvedUser);
    }

    @Override
    public void mergeUserItemIfExists(final User user, final UserBaseItem item) {
        /*
        item.setUser(userService.update(user, new User(item.getUserInfoForMerge())));
        if (!item.dataBindingsIsEmpty()) {
            this.dataBindingService.merge(user, item.getUser().getUserName(), item.getDataBindings());
        }
        this.addUserItemIfNotExists(user, item);*/
        throw new NotImplementedException();
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
        this.passaportService.removeUserFromAllPassaport((AdminOwner) user.getCurrentOwner(), userLoaded);
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
        final AggregationResults<BasicAggregateResultCount> results = this.mongoTemplate.aggregate(
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
                        Aggregation.count().as("result")
                ),
                this.documentNameConfig.getNameCollectionUserBase(),
                BasicAggregateResultCount.class
        );
        if (results == null || results.getUniqueMappedResult() == null || results.getUniqueMappedResult().getResult() == 0) {
            return false;
        }
        if (results.getUniqueMappedResult().getResult() > 1) {
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
        final AggregationResults<BasicAggregateResultCount> results = this.mongoTemplate.aggregate(
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
                        Aggregation.count().as("result")
                ),
                UserBase.class,
                BasicAggregateResultCount.class
        );
        if (results == null || results.getUniqueMappedResult() == null || results.getUniqueMappedResult().getResult() == 0) {
            return false;
        }
        if (results.getUniqueMappedResult().getResult() > 1) {
            //não pode retornar mais de um usuário
            throw new MuttleyException("Erro interno");
        }

        return true;
    }

    /* */

    /**
     * Verifica se o usuário já existe na base
     *//*
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
    }*/

    @Getter
    @Setter
    @Accessors(chain = true)
    private static class UserItemForAdd {
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

    protected final class ResultCount {
        private Long count;

        public Long getCount() {
            return count;
        }

        public ResultCount setCount(final Long count) {
            this.count = count;
            return this;
        }
    }
}
