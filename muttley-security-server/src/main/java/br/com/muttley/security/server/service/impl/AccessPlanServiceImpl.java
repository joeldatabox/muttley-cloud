package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyConflictException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.AccessPlan;
import br.com.muttley.model.security.User;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import br.com.muttley.security.server.repository.AccessPlanRepository;
import br.com.muttley.security.server.service.AccessPlanService;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

import static br.com.muttley.model.security.Role.ROLE_ACCESS_PLAN_CREATE;
import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

/**
 * @author Joel Rodrigues Moreira on 26/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class AccessPlanServiceImpl extends SecurityServiceImpl<AccessPlan> implements AccessPlanService {
    final AccessPlanRepository repository;
    final DocumentNameConfig documentNameConfig;
    private static final String[] basicRoles = new String[]{ROLE_ACCESS_PLAN_CREATE.getSimpleName()};

    @Autowired
    public AccessPlanServiceImpl(final AccessPlanRepository repository, final MongoTemplate template, DocumentNameConfig documentNameConfig) {
        super(repository, template, AccessPlan.class);
        this.repository = repository;
        this.documentNameConfig = documentNameConfig;
    }

    @Override
    public String[] getBasicRoles() {
        return basicRoles;
    }

    @Override
    public AccessPlan findByDescription(final String descricao) {
        final AccessPlan accessPlan = this.repository.findByDescription(descricao);
        if (accessPlan == null) {
            throw new MuttleyNotFoundException(AccessPlan.class, "descricao", "Registro não encontrado");
        }
        return accessPlan;
    }

    @Override
    public AccessPlan findByName(String name) {
        final AccessPlan accessPlan = this.repository.findByName(name);
        if (accessPlan == null) {
            throw new MuttleyNotFoundException(AccessPlan.class, "name", "Registro não encontrado");
        }
        return accessPlan;
    }

    @Override
    public AccessPlan findByOwner(String idOwner) {
        /**
         * db.getCollection("muttley-owners").aggregate([
         *     {$match:{"_id":ObjectId("632c4a6636f59f5538c3c8b6")}},
         *     {$lookup:{
         *         from:"muttley-access-plans",
         *         localField:"accessPlan.$id",
         *         foreignField:"_id",
         *         as:"accessPlan"
         *     }},
         *     {$replaceRoot:{newRoot:{$arrayElemAt:["$accessPlan", 0]}}}
         * ])
         */
        final List<AggregationOperation> operationList = new LinkedList<>();
        operationList.add(match(Criteria.where("_id").is(new ObjectId(idOwner))));
        operationList.add(context -> new BasicDBObject("$lookup",
                new BasicDBObject("from", this.documentNameConfig.getNameCollectionAccessPlan())
                        .append("localField", "accessPlan.$id")
                        .append("foreignField", "_id")
                        .append("as", "accessPlan")
        ));
        operationList.add(context -> new BasicDBObject("$replaceRoot",
                new BasicDBObject("newRoot",
                        new BasicDBObject("$arrayElemAt", asList("$accessPlan", 0))
                )
        ));
        final AggregationResults<AccessPlan> results = this.mongoTemplate.aggregate(
                newAggregation(operationList),
                this.documentNameConfig.getNameCollectionOwner(),
                AccessPlan.class
        );
        if (results == null || results.getUniqueMappedResult() == null) {
            throw new MuttleyNotFoundException(AccessPlan.class, "", "Registro não encontrado");
        }
        return results.getUniqueMappedResult();
    }

    @Override
    public void checkPrecondictionSave(final User user, final AccessPlan value) {
        //verificando se já existe um AccessPlan com a descrição informada
        try {
            findByDescription(value.getDescription());
            throw new MuttleyConflictException(clazz, "descricao", "Já existe um registro com essa descrição");
        } catch (MuttleyNotFoundException ex) {
        }
        super.checkPrecondictionSave(user, value);
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final AccessPlan value) {
        //verificando se a descricao alterada já existe
        try {
            if (!findByDescription(value.getDescription()).equals(value)) {
                throw new MuttleyConflictException(clazz, "descricao", "Já existe um registro com essa descrição");
            }
        } catch (MuttleyNotFoundException ex) {
        }
        super.checkPrecondictionUpdate(user, value);
    }
}
