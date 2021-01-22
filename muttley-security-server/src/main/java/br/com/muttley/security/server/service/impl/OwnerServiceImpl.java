package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.OwnerDataImpl;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import br.com.muttley.security.server.events.OwnerCreateEvent;
import br.com.muttley.security.server.repository.OwnerRepository;
import br.com.muttley.security.server.service.OwnerService;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.lookup;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.replaceRoot;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Service do owner do odin
 */
@Service
public class OwnerServiceImpl extends SecurityServiceImpl<Owner> implements OwnerService {
    private final OwnerRepository repository;
    private final ApplicationEventPublisher eventPublisher;
    private final DocumentNameConfig documentNameConfig;
    private static final String[] basicRoles = new String[]{"owner"};

    @Autowired
    public OwnerServiceImpl(final OwnerRepository repository, final MongoTemplate mongoTemplate, final ApplicationEventPublisher eventPublisher, final DocumentNameConfig documentNameConfig) {
        super(repository, mongoTemplate, Owner.class);
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.documentNameConfig = documentNameConfig;
    }

    @Override
    public String[] getBasicRoles() {
        return basicRoles;
    }

    @Override
    public void checkPrecondictionSave(final User user, final Owner value) {
        if (this.repository.existsByUserMaster(value.getUserMaster())) {
            throw new MuttleyBadRequestException(Owner.class, "userMaster", "Já existe um owner cadastrado com esse usuário master");
        }
    }

    @Override
    public Owner save(final User user, final Owner value) {
        if (value.getUserMaster() == null || value.getUserMaster().getId() == null) {
            throw new MuttleyBadRequestException(Owner.class, "userMaster", "Informe um usuário válido");
        }
        if (value.getAccessPlan() == null || value.getAccessPlan().getId() == null) {
            throw new MuttleyBadRequestException(Owner.class, "accessPlan", "Informe um plano de acesso");
        }
        final Owner salvedOwner = super.save(user, value);
        this.eventPublisher.publishEvent(new OwnerCreateEvent(salvedOwner));
        return salvedOwner;
    }

    @Override
    public Owner update(final User user, final Owner value) {
        if (value.getUserMaster() == null || value.getUserMaster().getId() == null) {
            throw new MuttleyBadRequestException(Owner.class, "userMaster", "Informe um usuário válido");
        }
        return super.update(user, value);
    }

    @Override
    public void checkPrecondictionDelete(final User user, final String id) {
        throw new MuttleyBadRequestException(Owner.class, "id", "Não é possível deletear um owner");
    }

    @Override
    public Owner findByName(final String name) {
        final Owner clienteOwner = repository.findByName(name);
        if (isNull(clienteOwner))
            throw new MuttleyNotFoundException(Owner.class, "name", "Registro não encontrado")
                    .addDetails("name", name);
        return clienteOwner;
    }

    @Override
    public List<? extends OwnerData> loadOwnersOfUser(final User user) {
        /**
         * db.getCollection("muttley-users-base").aggregate([
         *     {$match:{"users.user.$id":ObjectId("5feb26305c7fab2d6479b3ed")}},
         *     {$unwind:"$users"},
         *     {$match:{"users.user.$id":ObjectId("5feb26305c7fab2d6479b3ed")}},
         *     {$project:{owner:{$objectToArray:"$owner"}}},
         *     {$project:{owner:{$arrayElemAt:["$owner.v", 1]}}},
         *     {$lookup:{
         *         from: "muttley-owners",
         *         localField: "owner",
         *         foreignField:"_id",
         *         as: "owner"
         *     }},
         *     {$unwind:"$owner"},
         *     {$project:{_id:"$owner._id", name:"$owner.name", description:"$owner.description", userMaster:"$owner.userMaster"}}
         * ])
         */
        final AggregationResults<OwnerDataImpl> owners = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("users.user.$id").is(new ObjectId(user.getId()))),
                        unwind("$users"),
                        match(where("users.user.$id").is(new ObjectId(user.getId()))),
                        project().and(context -> new BasicDBObject("$objectToArray", "$owner")).as("owner"),
                        project().and(context -> new BasicDBObject("$arrayElemAt", asList("$owner.v", 1))).as("owner"),
                        lookup(this.documentNameConfig.getNameCollectionOwner(), "owner", "_id", "owner"),
                        unwind("$owner"),
                        project().and("$owner._id").as("_id")
                                .and("$owner.name").as("name")
                                .and("$owner.description").as("description")
                                .and("$owner.userMaster").as("userMaster")
                ),
                UserBase.class,
                OwnerDataImpl.class
        );
        if (owners == null || CollectionUtils.isEmpty(owners.getMappedResults())) {
            throw new MuttleyNoContentException(Owner.class, null, "Nenhum registro encontrado");
        }
        return owners.getMappedResults();
    }

    @Override
    public Owner findByUserAndId(final User user, final String id) {
        /**
         * db.getCollection("muttley-users-base").aggregate([
         *     {$match:{"owner.$id":ObjectId("5e28b3e3637e580001e465d6"), "users.user.$id":ObjectId("5feb26305c7fab2d6479b3ed")}},
         *     {$limit:1},
         *     {$project:{owner:{$objectToArray:"$owner"}}},
         *     {$project:{owner:{$arrayElemAt:["$owner.v", 1]}}},
         *     {$lookup:{
         *         from: "muttley-owners",
         *         localField: "owner",
         *         foreignField:"_id",
         *         as: "owner"
         *     }},
         *     {$unwind:"$owner"},
         *     {$replaceRoot:{newRoot:"$owner"}}
         * ])
         */
        final AggregationResults<Owner> owners = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("owner.$id").is(new ObjectId(id)).and("users.user.$id").is(new ObjectId(user.getId()))),
                        limit(1l),
                        project().and(context -> new BasicDBObject("$objectToArray", "$owner")).as("owner"),
                        project().and(context -> new BasicDBObject("$arrayElemAt", asList("$owner.v", 1))).as("owner"),
                        lookup(this.documentNameConfig.getNameCollectionOwner(), "owner", "_id", "owner"),
                        unwind("$owner"),
                        replaceRoot("$owner")
                ),
                UserBase.class,
                Owner.class
        );
        if (owners == null || owners.getUniqueMappedResult() == null) {
            throw new MuttleyBadRequestException(Owner.class, null, "Nenhum registro encontrado");
        }
        return owners.getUniqueMappedResult();
    }
}
