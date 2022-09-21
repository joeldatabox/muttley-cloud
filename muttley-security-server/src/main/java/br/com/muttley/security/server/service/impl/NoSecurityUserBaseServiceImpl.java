package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.service.Validator;
import br.com.muttley.exception.throwables.MuttleyException;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.OwnerData;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.UserBase;
import br.com.muttley.security.server.config.model.DocumentNameConfig;
import br.com.muttley.security.server.service.NoSecurityUserBaseService;
import br.com.muttley.security.server.service.PassaportService;
import br.com.muttley.security.server.service.UserDataBindingService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import static br.com.muttley.model.security.Role.ROLE_USER_BASE_CREATE;
import static java.util.Arrays.asList;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 26/11/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class NoSecurityUserBaseServiceImpl extends SecurityModelServiceImpl<UserBase> implements NoSecurityUserBaseService {
    private static final String[] basicRoles = new String[]{ROLE_USER_BASE_CREATE.getSimpleName()};
    private final String ODIN_USER;
    private final UserService userService;
    private final UserDataBindingService dataBindingService;
    private final DocumentNameConfig documentNameConfig;
    private final PassaportService passaportService;
    private final Validator validator;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public NoSecurityUserBaseServiceImpl(
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

    public UserBase save(final User user, final OwnerData owner, final UserBase value) {
        //somente usuario do serviço do odin podem fazer requisição para aqui
        //verificando se realmente está criando um novo registro
        checkIdForSave(value);
        //setando o dono do registro
        value.setOwner((Owner) owner);
        //garantindo que o metadata ta preenchido
        this.metadataService.generateNewMetadataFor(user, value);
        //processa regra de negocio antes de qualquer validação
        this.beforeSave(user, value);
        //verificando precondições
        this.checkPrecondictionSave(user, owner, value);
        //validando dados do objeto
        this.validator.validate(value);
        final UserBase salvedValue = saveByTemplate((Owner) owner, value);
        //realizando regras de enegocio depois do objeto ter sido salvo
        this.afterSave(user, salvedValue);
        //valor salvo
        return salvedValue;
    }

    private UserBase saveByTemplate(final Owner owner, final UserBase value) {
        //validateOwner(owner);
        value.setOwner(owner);
        //salvando o registro
        this.mongoTemplate.save(value);
        //pegando o registro salvo
        final AggregationResults<UserBase> results;
        if (!value.contaisObjectId()) {
            results = mongoTemplate.aggregate(
                    newAggregation(
                            asList(
                                    sort(DESC, "id"),
                                    limit(1)
                            )
                    ), this.clazz,
                    this.clazz
            );
        } else {
            results = mongoTemplate.aggregate(
                    newAggregation(
                            asList(
                                    match(where("owner.$id").is(owner.getObjectId()).and("id").is(value.getObjectId())),
                                    limit(1)
                            )
                    ), this.clazz,
                    this.clazz
            );
        }
        if (results == null || results.getUniqueMappedResult() == null) {
            throw new MuttleyException();
        }
        return results.getUniqueMappedResult();
    }
}
