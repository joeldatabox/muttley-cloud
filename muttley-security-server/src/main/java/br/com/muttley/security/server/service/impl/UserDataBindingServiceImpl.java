package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.service.Validator;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyConflictException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.headers.components.MuttleyCurrentTimezone;
import br.com.muttley.headers.components.MuttleyCurrentVersion;
import br.com.muttley.headers.components.MuttleyUserAgentName;
import br.com.muttley.localcache.services.LocalDatabindingService;
import br.com.muttley.model.BasicAggregateResult;
import br.com.muttley.model.BasicAggregateResultCount;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.VersionDocument;
import br.com.muttley.model.security.KeyUserDataBinding;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserData;
import br.com.muttley.model.security.UserDataBinding;
import br.com.muttley.model.security.events.UserResolverEvent;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import br.com.muttley.security.server.repository.UserDataBindingRepository;
import br.com.muttley.security.server.service.UserDataBindingService;
import com.mongodb.BasicDBObject;
import io.jsonwebtoken.lang.Collections;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.count;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira 12/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class UserDataBindingServiceImpl implements UserDataBindingService {
    @Autowired
    protected MuttleyCurrentTimezone currentTimezone;
    @Autowired
    protected MuttleyCurrentVersion currentVersion;
    @Autowired
    protected MuttleyUserAgentName userAgentName;
    private final MongoTemplate mongoTemplate;
    private final UserDataBindingRepository repository;
    private final DocumentNameConfig documentNameConfig;
    private final Validator validator;
    //private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;
    @Value("${muttley.security.check-roles:false}")
    private boolean checkRoles;

    private final LocalDatabindingService localDatabindingService;

    @Autowired
    public UserDataBindingServiceImpl(final MongoTemplate mongoTemplate, final UserDataBindingRepository repository, final DocumentNameConfig documentNameConfig, final Validator validator, final ApplicationEventPublisher eventPublisher, final LocalDatabindingService localDatabindingService) {
        this.mongoTemplate = mongoTemplate;
        this.repository = repository;
        this.documentNameConfig = documentNameConfig;
        this.validator = validator;
        this.eventPublisher = eventPublisher;
        this.localDatabindingService = localDatabindingService;
    }

    public boolean isCheckRole() {
        return checkRoles;
    }

    @Override
    public UserDataBinding save(final User user, final UserDataBinding dataBinding) {
        if (dataBinding.getUser() == null) {
            dataBinding.setUser(user);
        }
        checkBasicInfos(user, dataBinding);
        checkPrecondictionSave(user, dataBinding);
        final UserDataBinding salved = repository.save(dataBinding);
        this.localDatabindingService.expireUserDataBindings(user);
        return salved;
    }

    public void checkPrecondictionSave(final User user, final UserDataBinding dataBinding) {
        if (!user.equals(dataBinding.getUser())) {
            throw new MuttleyBadRequestException(UserDataBinding.class, "user", "O usuário informado é diferente do da requisição!");
        }
        //verificando se já não existe um registro com as informações
        this.checkIndex(user, dataBinding);
        this.validator.validate(dataBinding);
    }

    @Override
    public UserDataBinding update(final User user, final UserDataBinding dataBinding) {
        if (dataBinding.getUser() == null) {
            dataBinding.setUser(user);
        }
        checkBasicInfos(user, dataBinding);
        this.checkPrecondictionUpdate(user, dataBinding);
        final UserDataBinding salved = repository.save(dataBinding);
        this.localDatabindingService.expireUserDataBindings(user);
        return salved;
    }

    public void checkPrecondictionUpdate(final User user, final UserDataBinding dataBinding) {
        if (!user.equals(dataBinding.getUser())) {
            throw new MuttleyBadRequestException(UserDataBinding.class, "user", "O usuário informado é diferente do da requisição!");
        }
        //verificando se já não existe um registro com as informações
        this.checkIndex(user, dataBinding);
        this.validator.validate(dataBinding);
    }

    @Override
    public List<UserDataBinding> listByUserName(final User user, final String userName) {
        if (user.getUserName().equals(userName)) {
            return this.listBy(user);
        }
        /**
         * db.getCollection("muttley-users-databinding").aggregate([
         *     {$match:{"owner.$id":ObjectId("5e28b3e3637e580001e465d6")}},
         *     {$project:{_class:1, key:1, value:1, metadata:1, historic:1, owner:1, user:{$objectToArray:"$user"}}},
         *     {$project:{_class:1, key:1, value:1, metadata:1, historic:1, owner:1, user:{$arrayElemAt:["$user.v", 1]}}},
         *     {$lookup:{
         *         from:"muttley-users",
         *         localField:"user",
         *         foreignField:"_id",
         *         as: "user"
         *     }},
         *     {$unwind:"$user"},
         *     {$match:{"user.userName":"OwnerSiapi5e28b392637e580001e465d3"}}
         * ])
         */
        final AggregationResults<UserDataBinding> results = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId())),
                        project("key", "value", "metadata", "historic", "owner").and(context -> new BasicDBObject("$objectToArray", "$user")).as("user"),
                        project("key", "value", "metadata", "historic", "owner").and(context -> new BasicDBObject("$arrayElemAt", asList("$user.v", 1))).as("user"),
                        lookup(documentNameConfig.getNameCollectionUser(), "user", "_id", "user"),
                        unwind("$user"),
                        match(where("user.userName").is(userName))
                ),
                documentNameConfig.getNameCollectionUserDataBinding(),
                UserDataBinding.class
        );
        if (results == null || Collections.isEmpty(results.getMappedResults())) {
            return new ArrayList<>();
        }
        return results.getMappedResults();
    }

    @Override
    public List<UserDataBinding> listBy(final User user) {
        /**
         * db.getCollection("muttley-users-databinding").aggregate([
         *     {$match:{"owner.$id":ObjectId("5e28b3e3637e580001e465d6"),"user.$id":ObjectId("5e28b3e3637e580001e465d6")}},
         * ])
         */
        final Owner currentOwner = user.getCurrentOwner();
        final AggregationResults<UserDataBinding> results = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("owner.$id").is(currentOwner != null ? currentOwner.getObjectId() : null).and("user.$id").is(new ObjectId(user.getId())))
                ),
                UserDataBinding.class,
                UserDataBinding.class
        );
        if (results == null || Collections.isEmpty(results.getMappedResults())) {
            new ArrayList<>();
        }
        return results.getMappedResults();
    }

    @Override
    public UserDataBinding saveByUserName(final User user, final String userName, final UserDataBinding dataBinding) {
        if (dataBinding.getUser() == null) {
            dataBinding.setUser(this.findByUserName(userName));
        }
        checkBasicInfos(user, dataBinding);
        checkPrecondictionSaveByUserName(user, userName, dataBinding);

        final UserDataBinding salved = repository.save(dataBinding);
        this.localDatabindingService.expireUserDataBindings(user);
        return salved;
    }

    public void checkPrecondictionSaveByUserName(final User user, final String userName, final UserDataBinding dataBinding) {
        if (!userName.equals(dataBinding.getUser().getUserName())) {
            throw new MuttleyBadRequestException(UserDataBinding.class, "user", "O usuário informado é diferente do da requisição!");
        }
        this.checkIndex(user, userName, dataBinding);
        this.validator.validate(dataBinding);
    }

    @Override
    public UserDataBinding updateByUserName(final User user, final String userName, final UserDataBinding dataBinding) {
        if (dataBinding.getUser() == null) {
            dataBinding.setUser(this.findByUserName(userName));
        }
        if (StringUtils.isEmpty(dataBinding.getId())) {
            dataBinding.setId(this.loadIdFrom(user, dataBinding));
        }
        checkBasicInfos(user, dataBinding);
        checkPrecondictionUpdateByUserName(user, userName, dataBinding);

        final UserDataBinding salved = repository.save(dataBinding);
        this.localDatabindingService.expireUserDataBindings(user);
        return salved;
    }

    public void checkPrecondictionUpdateByUserName(final User user, final String userName, final UserDataBinding dataBinding) {
        if (!userName.equals(dataBinding.getUser().getUserName())) {
            throw new MuttleyBadRequestException(UserDataBinding.class, "user", "O usuário informado é diferente do da requisição!");
        }
        this.validator.validate(dataBinding);
        //verificando se já não existe um registro com as informações
        this.checkIndex(user, userName, dataBinding);
    }

    @Override
    public void merge(final User user, final String userName, final UserDataBinding dataBinding) {
        if (dataBinding.getUser() == null) {
            dataBinding.setUser(this.findByUserName(userName));
        }
        if (exists(user, dataBinding)) {

            this.updateByUserName(user, userName, dataBinding);
        } else {

            this.saveByUserName(user, userName, dataBinding);
        }
    }

    @Override
    public void merge(final User user, final String userName, final Set<UserDataBinding> dataBindings) {
        User userCurrent = null;
        for (UserDataBinding dataBinding : dataBindings) {
            if (dataBinding.getUser() == null) {
                if (userCurrent == null) {
                    if (user.getUserName().equals(userName)) {
                        userCurrent = user;
                    } else {
                        userCurrent = this.findByUserName(userName);
                    }
                }
                dataBinding.setUser(userCurrent);
            }
            if (exists(user, dataBinding)) {
                this.updateByUserName(user, userName, dataBinding);
            } else {
                this.saveByUserName(user, userName, dataBinding);
            }
        }
    }

    @Override
    public UserDataBinding getKey(User user, KeyUserDataBinding key) {
        return this.getKey(user, key.getKey());
    }

    @Override
    public UserDataBinding getKey(final User user, final String key) {
        /**
         * db.getCollection("muttley-users-databinding").aggregate([
         *     {$match:{"owner.$id":ObjectId("5e28b3e3637e580001e465d6"),"user.$id":ObjectId("5e28b3e3637e580001e465d6"), "key":"asdfasd"}},
         * ])
         */
        final AggregationResults<UserDataBinding> results = this.mongoTemplate.aggregate(
                newAggregation(
                        match(
                                where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                        .and("user.$id").is(new ObjectId(user.getId()))
                                        .and("key").is(key)
                        )
                ),
                UserDataBinding.class,
                UserDataBinding.class
        );
        if (results == null || results.getUniqueMappedResult() == null) {
            return null;
        }
        return results.getUniqueMappedResult();
    }

    @Override
    public UserDataBinding getKeyByUserName(User user, String userName, KeyUserDataBinding key) {
        return this.getKeyByUserName(user, userName, key.getKey());
    }

    @Override
    public UserDataBinding getKeyByUserName(final User user, final String userName, final String key) {
        if (user.getUserName().equals(userName)) {
            return this.getKey(user, key);
        }
        /**
         * db.getCollection("muttley-users-databinding").aggregate([
         *     {$match:{"owner.$id":ObjectId("5e28b3e3637e580001e465d6"), "key":"asdfasd"}},
         *     {$project:{_class:1, key:1, value:1, metadata:1, historic:1, owner:1, user:{$objectToArray:"$user"}}},
         *     {$project:{_class:1, key:1, value:1, metadata:1, historic:1, owner:1, user:{$arrayElemAt:["$user.v", 1]}}},
         *     {$lookup:{
         *         from:"muttley-users",
         *         localField:"user",
         *         foreignField:"_id",
         *         as: "user"
         *     }},
         *     {$unwind:"$user"},
         *     {$match:{"user.userName":"OwnerSiapi5e28b392637e580001e465d3", "key":"asdfasd"}}
         * ])
         */
        final AggregationResults<UserDataBinding> results = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId()).and("key").is(key)),
                        project("key", "value", "metadata", "historic", "owner").and(context -> new BasicDBObject("$objectToArray", "$user")).as("user"),
                        project("key", "value", "metadata", "historic", "owner").and(context -> new BasicDBObject("$arrayElemAt", asList("$user.v", 1))).as("user"),
                        lookup(documentNameConfig.getNameCollectionUser(), "user", "_id", "user"),
                        unwind("$user"),
                        match(where("user.userName").is(userName).and("key").is(key))
                ),
                documentNameConfig.getNameCollectionUserDataBinding(),
                UserDataBinding.class
        );
        if (results == null || results.getUniqueMappedResult() == null) {
            return null;
        }
        return results.getUniqueMappedResult();
    }

    @Override
    public boolean contains(User user, KeyUserDataBinding key) {
        return this.contains(user, key.getKey());
    }

    @Override
    public boolean contains(final User user, final String key) {
        /**
         * db.getCollection("muttley-users-databinding").aggregate([
         *     {$match:{"owner.$id":ObjectId("5e28b3e3637e580001e465d6"),"user.$id":ObjectId("5e28b3e3637e580001e465d6"), "key":"asdfasd"}},
         * ])
         */
        return this.mongoTemplate.exists(
                new Query(
                        where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                .and("user.$id").is(new ObjectId(user.getId()))
                                .and("key").is(key)
                ), UserDataBinding.class);
    }

    @Override
    public boolean containsByUserNameAndKey(User user, String userName, KeyUserDataBinding key) {
        return this.containsByUserNameAndKey(user, userName, key);
    }

    @Override
    public boolean containsByUserNameAndKey(final User user, final String userName, final String key) {
        if (user.getUserName().equals(userName)) {
            return this.contains(user, key);
        }
        /**
         * db.getCollection("muttley-users-databinding").aggregate([
         *     {$match:{"owner.$id":ObjectId("5e28b3e3637e580001e465d6"), "key":"asdfasd"}},
         *     {$project:{_class:1, key:1, value:1, metadata:1, historic:1, owner:1, user:{$objectToArray:"$user"}}},
         *     {$project:{_class:1, key:1, value:1, metadata:1, historic:1, owner:1, user:{$arrayElemAt:["$user.v", 1]}}},
         *     {$lookup:{
         *         from:"muttley-users",
         *         localField:"user",
         *         foreignField:"_id",
         *         as: "user"
         *     }},
         *     {$unwind:"$user"},
         *     {$match:{"user.userName":"OwnerSiapi5e28b392637e580001e465d3", "key":"asdfasd"}}
         * ])
         */
        final AggregationResults<BasicAggregateResultCount> results = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId()).and("key").is(key)),
                        project("key", "value", "metadata", "historic", "owner").and(context -> new BasicDBObject("$objectToArray", "$user")).as("user"),
                        project("key", "value", "metadata", "historic", "owner").and(context -> new BasicDBObject("$arrayElemAt", asList("$user.v", 1))).as("user"),
                        lookup(documentNameConfig.getNameCollectionUser(), "user", "_id", "user"),
                        unwind("$user"),
                        match(where("user.userName").is(userName).and("key").is(key)),
                        count().as("result")
                ),
                documentNameConfig.getNameCollectionUserDataBinding(),
                BasicAggregateResultCount.class
        );
        if (results == null || results.getUniqueMappedResult() == null) {
            throw new MuttleyNoContentException(UserDataBinding.class, "userName", "Nenhum registro encontrado para o usuário desejado");
        }
        return results.getUniqueMappedResult().getResult() > 0;
    }

    @Override
    public boolean containsByKeyAndValue(User user, KeyUserDataBinding key, String value) {
        return this.containsByKeyAndValue(user, key.getKey(), value);
    }

    @Override
    public boolean containsByKeyAndValue(final User user, final String key, final String value) {
        /**
         * db.getCollection("muttley-users-databinding").aggregate([
         *     {$match:{"owner.$id":ObjectId("5e28b3e3637e580001e465d6"), "key":"UserColaborador", value:"5ff65e339208bc0007c0d6ba"}},
         *     {}
         * ])
         */
        final AggregationResults<BasicAggregateResultCount> results = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId()).and("key").is(key).and("value").is(value)),
                        count().as("result")
                ),
                documentNameConfig.getNameCollectionUserDataBinding(),
                BasicAggregateResultCount.class
        );
        if (results == null || results.getUniqueMappedResult() == null) {
            throw new MuttleyNoContentException(UserDataBinding.class, "userName", "Erro na consulta");
        }
        return results.getUniqueMappedResult().getResult() > 0;
    }

    @Override
    public boolean containsByKeyAndValueAndUserNameNotEq(User user, String userName, KeyUserDataBinding key, String value) {
        return this.containsByKeyAndValueAndUserNameNotEq(user, userName, key.getKey(), value);
    }

    @Override
    public boolean containsByKeyAndValueAndUserNameNotEq(final User user, final String userName, final String key, final String value) {
        /**
         * db.getCollection("muttley-users-databinding").aggregate([
         *
         *     {$match:{"owner.$id":ObjectId("5e28b3e3637e580001e465d6"), "key":"UserColaborador", value:"5ff65e339208bc0007c0d6ba"}},
         *     {$project:{_class:1, key:1, value:1, metadata:1, historic:1, owner:1, user:{$objectToArray:"$user"}}},
         *     {$project:{_class:1, key:1, value:1, metadata:1, historic:1, owner:1, user:{$arrayElemAt:["$user.v", 1]}}},
         *     {$lookup:{
         *         from:"muttley-users",
         *         localField:"user",
         *         foreignField:"_id",
         *         as: "user"
         *     }},
         *     {$unwind:"$user"},
         *     {$match:{"user.userName":{$ne:"0756143600010711000523BRUNA.Ab"}}}
         * ])
         */
        final List<AggregationOperation> operations = new LinkedList<>(
                asList(
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId()).and("key").is(key).and("value").is(value))
                )
        );
        /*if (!StringUtils.isEmpty(userName)) {
            operations.addAll(
                    asList(
                            project("key", "value", "metadata", "historic", "owner").and(context -> new BasicDBObject("$objectToArray", "$user")).as("user"),
                            project("key", "value", "metadata", "historic", "owner").and(context -> new BasicDBObject("$arrayElemAt", asList("$user.v", 1))).as("user"),
                            lookup(documentNameConfig.getNameCollectionUser(), "user", "_id", "user"),
                            unwind("$user")
                            //match(where("user.userName").ne(userName).and("key").is(key))
                    )
            );
        }*/
        //operations.add(count().as("result"));
        final AggregationResults<UserDataBinding> results = this.mongoTemplate.aggregate(
                newAggregation(
                        operations
                ),
                documentNameConfig.getNameCollectionUserDataBinding(),
                UserDataBinding.class
        );
        if (results == null || results.getUniqueMappedResult() == null) {
            //se não encontrou nada é sinal que o databinding está disponivel
            return false;
        } else {
            return results.getMappedResults()
                    .parallelStream()
                    .filter(it -> !it.getUser().getUserName().equals(userName))
                    .count() > 0;
        }
    }

    @Override
    public UserData getUserBy(final User user, final KeyUserDataBinding key, final String value) {
        return this.getUserBy(user, key.getKey(), value);
    }

    @Override
    public UserData getUserBy(final User user, final String key, final String value) {
        /**
         * db.getCollection("muttley-users-databinding").aggregate([
         *     {$match:{"owner.$id": ObjectId("5e28b3e3637e580001e465d6"), "key":"UserColaborador", "value":"5e28bcf86f985c00017e7a28"}},
         *     {$project:{user:1}}
         * ])
         */
        final AggregationResults<UserDataBinding> results = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId()).and("key").is(key).and("value").is(value))
                ),
                UserDataBinding.class,
                UserDataBinding.class
        );
        if (results == null || results.getUniqueMappedResult() == null) {
            return null;
        }
        return results.getUniqueMappedResult().getUser();
    }

    private final void checkIndex(final User user, final UserDataBinding dataBinding) {
        if (StringUtils.isEmpty(dataBinding.getId())) {
            if (this.exists(user, dataBinding)) {
                throw new MuttleyConflictException(UserDataBinding.class, "key", "Jás existe um registro com essas informações");
            }
        } else {
            if (this.mongoTemplate.exists(
                    new Query(
                            where("id").ne(dataBinding.getObjectId())
                                    .and("owner.$id").is(user.getCurrentOwner().getObjectId())
                                    .and("user.$id").is(new ObjectId(dataBinding.getUser().getId()))
                                    .and("key").is(dataBinding.getKey())
                    ), UserDataBinding.class)) {
                throw new MuttleyConflictException(UserDataBinding.class, "key", "Jás existe um registro com essas informações");
            }
        }

        if (dataBinding.getKey().isUnique()) {
            //verificando se outro usário já tem esse databinding
            if (this.containsByKeyAndValueAndUserNameNotEq(user, user.getUserName(), dataBinding.getKey().getKey(), dataBinding.getValue())) {
                throw new MuttleyBadRequestException(UserDataBinding.class, "key", "Já existe um usuário que possui ligação com " + dataBinding.getKey().getDisplayKey() + " informado(a)");
            }
        }
    }

    private final void checkIndex(final User user, final String userName, final UserDataBinding dataBinding) {
        if (user.getUserName().equals(userName)) {
            this.checkIndex(user, dataBinding);
        } else {

            //caso o registro não tenha id é sinal que estamos inserindo um novo
            //com isso se faz necessário verificar se já não existe o mesmo
            if (!dataBinding.contaisObjectId()) {
                if (this.exists(user, userName, dataBinding.getKey().getKey())) {
                    throw new MuttleyConflictException(UserDataBinding.class, "key", "Jás existe um registro com essas informações");
                }
            }

            if (dataBinding.getKey().isUnique()) {
                //verificando se outro usário já tem esse databinding
                if (this.containsByKeyAndValueAndUserNameNotEq(user, userName, dataBinding.getKey().getKey(), dataBinding.getValue())) {
                    throw new MuttleyBadRequestException(UserDataBinding.class, "key", "Já existe um usuário que possui ligação com " + dataBinding.getKey().getDisplayKey() + " informado(a)");
                }
            }
        }
    }

    private boolean exists(final User user, UserDataBinding dataBinding) {
        return this.repository.exists("owner.$id", user.getCurrentOwner().getObjectId(), "user.$id", new ObjectId(dataBinding.getUser().getId()), "key", dataBinding.getKey().getKey());
    }

    private boolean exists(final User user, final String userName, final String key) {
        /**
         * db.getCollection("muttley-users-databinding").aggregate([
         *     {$match:{"owner.$id":ObjectId("5e28b3e3637e580001e465d6")}},
         *     {$project:{_class:1, key:1, value:1, metadata:1, historic:1, owner:1, user:{$objectToArray:"$user"}}},
         *     {$project:{_class:1, key:1, value:1, metadata:1, historic:1, owner:1, user:{$arrayElemAt:["$user.v", 1]}}},
         *     {$lookup:{
         *         from:"muttley-users",
         *         localField:"user",
         *         foreignField:"_id",
         *         as: "user"
         *     }},
         *     {$unwind:"$user"},
         *     {$match:{"user.userName":"OwnerSiapi5e28b392637e580001e465d3"}}
         * ])
         */
        final AggregationResults<BasicAggregateResultCount> results = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId())),
                        project("key", "value").and(context -> new BasicDBObject("$objectToArray", "$user")).as("user"),
                        project("key", "value").and(context -> new BasicDBObject("$arrayElemAt", asList("$user.v", 1))).as("user"),
                        lookup(documentNameConfig.getNameCollectionUser(), "user", "_id", "user"),
                        unwind("$user"),
                        match(where("user.userName").is(userName)),
                        count().as("result")
                ),
                documentNameConfig.getNameCollectionUserDataBinding(),
                BasicAggregateResultCount.class
        );

        return results != null && results.getUniqueMappedResult() != null && results.getUniqueMappedResult().getResult() > 0;
    }

    private String loadIdFrom(final User user, final UserDataBinding dataBinding) {
        final AggregationResults<BasicAggregateResult> results = this.mongoTemplate.aggregate(
                newAggregation(
                        match(
                                where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                        .and("user.$id").is(new ObjectId(dataBinding.getUser().getId()))
                                        .and("key").is(dataBinding.getKey())
                        ),
                        project().and("_id").as("result")
                ),
                documentNameConfig.getNameCollectionUserDataBinding(),
                BasicAggregateResult.class
        );
        return results.getUniqueMappedResult().getResult().toString();
    }

    private void checkBasicInfos(final User user, final UserDataBinding userDataBinding) {
        userDataBinding.setOwner(user.getCurrentOwner());
        if (StringUtils.isEmpty(userDataBinding.getId())) {
            userDataBinding.setHistoric(this.createHistoric(user));
            this.createMetaData(user, userDataBinding);
        } else {
            this.generateHistoricUpdate(user, this.repository.loadHistoric(userDataBinding), userDataBinding);
            this.generateMetaDataUpdate(user, this.repository.loadMetadata(userDataBinding), userDataBinding);
        }
    }

    protected void createMetaData(final User user, final UserDataBinding value) {
        //se não tiver nenhum metadata criado, vamos criar um
        if (!value.containsMetadata()) {
            value.setMetadata(new MetadataDocument()
                    .setTimeZones(this.currentTimezone.getCurrentTimezoneDocument())
                    .setVersionDocument(
                            new VersionDocument()
                                    .setOriginVersionClientCreate(this.currentVersion.getCurrentValue())
                                    .setOriginVersionClientLastUpdate(this.currentVersion.getCurrentValue())
                                    .setOriginNameClientCreate(this.userAgentName.getCurrentValue())
                                    .setOriginNameClientLastUpdate(this.userAgentName.getCurrentValue())
                                    .setServerVersionCreate(this.currentVersion.getCurrenteFromServer())
                                    .setServerVersionLastUpdate(this.currentVersion.getCurrenteFromServer())
                    ));
        } else {
            //se não tem um timezone válido, vamos criar um
            if (!value.getMetadata().containsTimeZones()) {
                value.getMetadata().setTimeZones(this.currentTimezone.getCurrentTimezoneDocument());
            } else {
                //se chegou aqui é sinal que já possui infos de timezones e devemos apenas checar e atualizar caso necessário

                //O timezone atual informado é valido?
                if (value.getMetadata().getTimeZones().isValidCurrentTimeZone()) {
                    //adicionado a mesma info no createTimezone já que estamos criando um novo registro
                    value.getMetadata().getTimeZones().setCreateTimeZone(value.getMetadata().getTimeZones().getCurrentTimeZone());
                }

                //adicionando infos de timezone do servidor
                final String currentServerTimezone = this.currentTimezone.getCurrenteTimeZoneFromServer();
                value.getMetadata().getTimeZones().setServerCreteTimeZone(currentServerTimezone);
                value.getMetadata().getTimeZones().setServerCurrentTimeZone(currentServerTimezone);
            }

            //criando version valido
            value.getMetadata().setVersionDocument(
                    new VersionDocument()
                            .setOriginVersionClientCreate(this.currentVersion.getCurrentValue())
                            .setOriginVersionClientLastUpdate(this.currentVersion.getCurrentValue())
                            .setOriginNameClientCreate(this.userAgentName.getCurrentValue())
                            .setOriginNameClientLastUpdate(this.userAgentName.getCurrentValue())
                            .setServerVersionCreate(this.currentVersion.getCurrenteFromServer())
                            .setServerVersionLastUpdate(this.currentVersion.getCurrenteFromServer())
            );


        }
    }

    protected Historic createHistoric(final User user) {
        final Date now = new Date();
        return new Historic()
                .setCreatedBy(user)
                .setDtCreate(now)
                .setLastChangeBy(user)
                .setDtChange(now);
    }

    protected void generateMetaDataUpdate(final User user, final MetadataDocument currentMetadata, final UserDataBinding value) {
        currentMetadata.getTimeZones().setServerCurrentTimeZone(this.currentTimezone.getCurrenteTimeZoneFromServer());


        //se veio informações no registro, devemos aproveitar
        if (value.containsMetadata()) {
            if (value.getMetadata().containsTimeZones()) {
                if (value.getMetadata().getTimeZones().isValidCurrentTimeZone()) {
                    currentMetadata.getTimeZones().setCurrentTimeZone(value.getMetadata().getTimeZones().getCurrentTimeZone());
                } else {
                    currentMetadata.getTimeZones().setCurrentTimeZone(this.currentTimezone.getCurrentValue());
                }
            } else {
                currentMetadata.getTimeZones().setCurrentTimeZone(this.currentTimezone.getCurrentValue());
            }
        } else {
            currentMetadata.getTimeZones().setCurrentTimeZone(this.currentTimezone.getCurrentValue())
                    .setServerCurrentTimeZone(this.currentTimezone.getCurrenteTimeZoneFromServer());
        }
        //setando versionamento
        currentMetadata
                .getVersionDocument()
                .setServerVersionLastUpdate(this.currentVersion.getCurrenteFromServer())
                .setOriginNameClientLastUpdate(this.userAgentName.getCurrentValue())
                .setOriginVersionClientLastUpdate(this.currentVersion.getCurrentValue());

        value.setMetadata(currentMetadata);
    }

    protected void generateHistoricUpdate(final User user, final Historic historic, final UserDataBinding userDataBinding) {
        userDataBinding.setHistoric(historic
                .setLastChangeBy(user)
                .setDtChange(new Date()));
    }

    protected User findByUserName(final String userName) {
        final UserResolverEvent event = new UserResolverEvent(userName);
        this.eventPublisher.publishEvent(event);
        return event.getUserResolver();
    }
}
