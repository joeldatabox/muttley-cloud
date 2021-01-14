package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.service.Validator;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyConflictException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.model.BasicAggregateResult;
import br.com.muttley.model.BasicAggregateResultCount;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserDataBinding;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import br.com.muttley.security.server.repository.UserDataBindingRepository;
import br.com.muttley.security.server.service.UserDataBindingService;
import br.com.muttley.security.server.service.UserService;
import com.mongodb.BasicDBObject;
import io.jsonwebtoken.lang.Collections;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static br.com.muttley.model.security.Role.ROLE_USER_DATA_BINDING_READ;
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
    private static final String[] basicRoles = new String[]{ROLE_USER_DATA_BINDING_READ.getSimpleName()};
    private final MongoTemplate mongoTemplate;
    private final UserDataBindingRepository repository;
    private final DocumentNameConfig documentNameConfig;
    private final Validator validator;
    private final UserService userService;

    @Autowired
    public UserDataBindingServiceImpl(final MongoTemplate mongoTemplate, final UserDataBindingRepository repository, final DocumentNameConfig documentNameConfig, final Validator validator, final UserService userService) {
        this.mongoTemplate = mongoTemplate;
        this.repository = repository;
        this.documentNameConfig = documentNameConfig;
        this.validator = validator;
        this.userService = userService;
    }

    @Override
    public UserDataBinding save(final User user, final UserDataBinding dataBinding) {
        if (dataBinding.getUser() == null) {
            dataBinding.setUser(user);
        }
        checkPrecondictionSave(user, dataBinding);
        return repository.save(dataBinding);
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
        this.checkPrecondictionUpdate(user, dataBinding);
        return null;
    }

    public void checkPrecondictionUpdate(final User user, final UserDataBinding dataBinding) {
        if (!user.equals(dataBinding.getUser())) {
            throw new MuttleyBadRequestException(UserDataBinding.class, "user", "O usuário informado é diferente do da requisição!");
        }
        //verificando se já não existe um registro com as informações
        this.checkIndex(user, dataBinding);
    }

    @Override
    public List<UserDataBinding> listByUserName(final User user, final String userName) {

        final AggregationResults<UserDataBinding> results;

        if (user.getUserName().equals(userName)) {
            /**
             * db.getCollection("muttley-users-databinding").aggregate([
             *     {$match:{"owner.$id":ObjectId("5e28b3e3637e580001e465d6"),"user.$id":ObjectId("5e28b3e3637e580001e465d6")}},
             * ])
             */
            results = this.mongoTemplate.aggregate(
                    newAggregation(
                            match(where("owner.$id").is(user.getCurrentOwner().getObjectId()).and("user.$id").is(new ObjectId(user.getId())))
                    ),
                    UserDataBinding.class,
                    UserDataBinding.class
            );
        } else {
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
            results = this.mongoTemplate.aggregate(
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
        }

        if (results == null || Collections.isEmpty(results.getMappedResults())) {
            throw new MuttleyNoContentException(UserDataBinding.class, "userName", "Nenhum registro encontrado para o usuário desejado");
        }
        return results.getMappedResults();
    }

    @Override
    public UserDataBinding saveByUserName(final User user, final String userName, final UserDataBinding dataBinding) {
        if (dataBinding.getUser() == null) {
            dataBinding.setUser(this.userService.findByUserName(userName));
        }
        checkPrecondictionSave(user, dataBinding);
        return repository.save(dataBinding);
    }

    @Override
    public UserDataBinding updateByUserName(final User user, final String userName, final UserDataBinding dataBinding) {
        if (dataBinding.getUser() == null) {
            dataBinding.setUser(this.userService.findByUserName(userName));
        }
        checkPrecondictionUpdate(user, dataBinding);
        return repository.save(dataBinding);
    }

    @Override
    public void merge(final User user, final UserDataBinding dataBinding) {
        this.validator.validate(dataBinding);
        if (exists(user, dataBinding)) {
            this.repository.save(dataBinding.setId(this.loadIdFrom(user, dataBinding)));
        } else {
            this.repository.save(dataBinding);
        }
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
    }

    private boolean exists(final User user, UserDataBinding dataBinding) {
        return this.repository.exists("owner.$id", user.getCurrentOwner().getObjectId(), "user.$id", new ObjectId(dataBinding.getUser().getId()), "key", dataBinding.getKey());
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
}
