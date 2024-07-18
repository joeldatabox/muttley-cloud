package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.service.impl.ServiceImpl;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.exception.throwables.security.MuttleySecurityNotFoundException;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserView;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import br.com.muttley.security.server.service.UserViewService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author Joel Rodrigues Moreira on 29/04/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class UserViewServiceImpl extends ServiceImpl<UserView> implements UserViewService {
    private static final String[] basicRoles = new String[]{"user_view"};
    private final DocumentNameConfig documentNameConfig;

    @Autowired
    public UserViewServiceImpl(final MongoTemplate mongoTemplate, final DocumentNameConfig documentNameConfig) {
        super(null, mongoTemplate, UserView.class);
        this.documentNameConfig = documentNameConfig;
    }

    @Override
    public String[] getBasicRoles() {
        return basicRoles;
    }

    @Override
    public void checkPrecondictionSave(final User user, final UserView value) {
        throw new MuttleyBadRequestException(UserView.class, null, "Não se pode inserir registro em uma view");
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final UserView value) {
        throw new MuttleyBadRequestException(UserView.class, null, "Não se pode alterar registro em uma view");
    }

    @Override
    public void checkPrecondictionDelete(final User user, final String id) {
        throw new MuttleyBadRequestException(UserView.class, null, "Não se pode deletar registro em uma view");
    }

    @Override
    public UserView findById(final User user, final String id) {
        final UserView view = this.mongoTemplate.findOne(
                query(where("_id").is(new ObjectId(id))),
                UserView.class,
                this.documentNameConfig.getNameViewCollectionUser()
        );
        if (view == null) {
            throw new MuttleyNotFoundException(UserView.class, "id", "Registro não encontrado");
        }
        return view;
    }

    @Override
    public UserView findFirst(final User user) {
        final UserView view = this.mongoTemplate.aggregate(
                newAggregation(
                        limit(1)
                ), this.documentNameConfig.getNameViewCollectionUser(), UserView.class
        ).getUniqueMappedResult();
        if (view == null) {
            throw new MuttleyNotFoundException(UserView.class, null, "Registro não encontrado");
        }
        return view;
    }

    @Override
    public Long count(final User user, final Map<String, String> allRequestParams) {
        throw new NotImplementedException();
    }

    @Override
    public List<UserView> findAll(final User user, final Map<String, String> allRequestParams) {
        throw new NotImplementedException();
    }

    @Override
    public UserView findByUserName(final String userName, final String idOwner) {
        final List<AggregationOperation> operations = new LinkedList<>(asList(
                match(
                        new Criteria().orOperator(
                                where("name").is(userName),
                                where("userName").is(userName),
                                where("email").is(userName),
                                where("nickUsers").is(userName)
                        )
                )
        ));

        if (!StringUtils.isEmpty(idOwner)) {
            operations.add(
                    match(
                            where("owner.$id").is(new ObjectId(idOwner))
                    )
            );
        }

        this.mongoTemplate.aggregate(newAggregation(operations), this.documentNameConfig.getNameViewCollectionUser(), UserView.class);

        final AggregationResults<UserView> results = this.mongoTemplate.aggregate(
                newAggregation(operations), this.documentNameConfig.getNameViewCollectionUser(), UserView.class
        );
        if (results == null || results.getUniqueMappedResult() == null) {
            throw new MuttleyNotFoundException(UserView.class, "userName", "Usuário não encontrado");
        }
        return results.getUniqueMappedResult();
    }

    @Override
    public List<UserView> list(final String criterio, final String idOwner) {
        final List<AggregationOperation> operations = this.createQuery(criterio, idOwner);
        if (operations.isEmpty()) {
            operations.add(project("_id", "name", "userName", "email", "nickUsers", "owner"));
        }
        final List<UserView> views = this.mongoTemplate.aggregate(
                newAggregation(operations), this.documentNameConfig.getNameViewCollectionUser(), UserView.class
        ).getMappedResults();
        if (views == null || views.isEmpty()) {
            throw new MuttleyNoContentException(UserView.class, "", "nenhum registro encontrado");
        }
        return views;
    }




    @Override
    public long count(final String criterio, final String idOwner) {
        final List<AggregationOperation> operations = this.createQuery(criterio, idOwner);
        operations.add(Aggregation.count().as("count"));
        final AggregationResults<ResultCount> result = this.mongoTemplate.aggregate(
                newAggregation(operations),
                this.documentNameConfig.getNameViewCollectionUser(),
                ResultCount.class
        );
        return result.getUniqueMappedResult() != null ? result.getUniqueMappedResult().getCount() : 0L;
    }

    private List<AggregationOperation> createQuery(final String criterio, final String idOwner) {
        final List<AggregationOperation> operations = new ArrayList<>(2);
        if (!StringUtils.isEmpty(criterio)) {
            operations.add(
                    match(
                            new Criteria().orOperator(
                                    where("name").regex(criterio, "si"),
                                    where("userName").regex(criterio, "si"),
                                    where("email").regex(criterio, "si"),
                                    where("nickUsers").in(criterio)
                            )
                    )
            );
        }
        if (!StringUtils.isEmpty(idOwner)) {
            operations.add(
                    match(
                            where("owner.$id").is(new ObjectId(idOwner))
                    )
            );
        }
        return operations;
    }

    protected final class ResultCount {
        private Long count;

        protected ResultCount() {
        }

        public Long getCount() {
            return this.count;
        }

        public ResultCount setCount(Long count) {
            this.count = count;
            return this;
        }
    }
}
