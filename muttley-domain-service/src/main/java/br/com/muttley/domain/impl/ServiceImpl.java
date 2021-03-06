package br.com.muttley.domain.impl;

import br.com.muttley.domain.Service;
import br.com.muttley.domain.Validator;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNoContentException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.headers.components.MuttleyCurrentTimezone;
import br.com.muttley.headers.components.MuttleyCurrentVersion;
import br.com.muttley.headers.components.MuttleyUserAgentName;
import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.VersionDocument;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.repository.SimpleTenancyMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static br.com.muttley.model.Document.getPropertyFrom;
import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

/**
 * @author Joel Rodrigues Moreira on 30/01/18.
 * @project muttley-cloud
 */
public abstract class ServiceImpl<T extends Document> implements Service<T> {

    protected final SimpleTenancyMongoRepository<T> repository;
    protected final Class<T> clazz;
    @Value("${muttley.security.check-roles:false}")
    private boolean checkRoles;
    protected final MongoTemplate mongoTemplate;

    @Autowired
    protected MuttleyCurrentTimezone currentTimezone;
    @Autowired
    protected MuttleyCurrentVersion currentVersion;
    @Autowired
    protected MuttleyUserAgentName userAgentName;

    @Autowired
    protected Validator validator;

    public ServiceImpl(final SimpleTenancyMongoRepository<T> repository, final MongoTemplate mongoTemplate, final Class<T> clazz) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.clazz = clazz;
    }

    @Override
    public boolean isCheckRole() {
        return this.checkRoles;
    }

    public String[] getBasicRoles() {
        return new String[]{""};
    }

    @Override
    public void checkPrecondictionSave(final User user, final T value) {
    }

    @Override
    public void beforeSave(final User user, final T value) {
    }

    @Override
    public T save(final User user, final T value) {
        //verificando se realmente está criando um novo registro
        if (!StringUtils.isEmpty(value.getId())) {
            throw new MuttleyBadRequestException(clazz, "id", "Não é possível criar um registro com um id existente");
        }
        value.setId(null);
        //garantindo que o históriconão ficará nulo
        value.setHistoric(this.createHistoric(user));
        //garantindo que o metadata ta preenchido
        this.createMetaData(user, value);
        //processa regra de negocio antes de qualquer validação
        this.beforeSave(user, value);
        //verificando precondições
        this.checkPrecondictionSave(user, value);
        //validando dados do objeto
        this.validator.validate(value);
        final T otherValue = repository.save(value);
        //realizando regras de enegocio depois do objeto ter sido salvo
        this.afterSave(user, otherValue);
        //valor salvo
        return otherValue;
    }

    @Override
    public void afterSave(final User user, final T value) {
    }

    @Override
    public void checkPrecondictionUpdate(final User user, final T value) {

    }

    @Override
    public void beforeUpdate(final User user, final T value) {

    }

    @Override
    public T update(final User user, final T value) {
        //verificando se realmente está alterando um registro
        if (value.getId() == null) {
            throw new MuttleyBadRequestException(clazz, "id", "Não é possível alterar um registro sem informar um id válido");
        }
        //verificando se o registro realmente existe
        if (!this.repository.exists(value.getId())) {
            throw new MuttleyNotFoundException(clazz, "id", "Registro não encontrado");
        }
        //gerando histórico de alteração
        value.setHistoric(generateHistoricUpdate(user, repository.loadHistoric(value)));
        //gerando metadata de alteração
        generateMetaDataUpdate(user, value);
        //processa regra de negocio antes de qualquer validação
        beforeUpdate(user, value);
        //verificando precondições
        checkPrecondictionUpdate(user, value);
        //validando dados
        this.validator.validate(value);
        final T otherValue = repository.save(value);
        //realizando regras de enegocio depois do objeto ter sido alterado
        afterUpdate(user, value);
        return otherValue;
    }

    @Override
    public void afterUpdate(final User user, final T value) {

    }

    @Override
    public T findById(final User user, final String id) {
        if (isNull(id)) {
            throw new MuttleyBadRequestException(clazz, "id", "informe um id válido");
        }

        final Optional<T> result = this.repository.findById(id);
        if (!result.isPresent()) {
            throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
        }
        return result.get();
    }

    @Override
    public Set<T> findByIds(final User user, final String[] ids) {
        if (ObjectUtils.isEmpty(ids)) {
            throw new MuttleyBadRequestException(clazz, "id", "informe pelo menos um id válido");
        }
        if (ids.length > 50) {
            throw new MuttleyBadRequestException(clazz, "ids", "Quantidade máxima excedida")
                    .addDetails("min", 1)
                    .addDetails("max", 50);
        }
        final Set<T> records = this.repository.findMulti(ids);
        if (records == null) {
            return emptySet();
        }
        return records;
    }

    @Override
    public T findFirst(final User user) {
        final T result = this.repository.findFirst();
        if (isNull(result)) {
            throw new MuttleyNotFoundException(clazz, "user", "Nenhum registro encontrado");
        }
        return result;
    }

    @Override
    public Historic loadHistoric(final User user, final String id) {
        final Historic historic = this.repository.loadHistoric(id);
        if (isNull(historic)) {
            throw new MuttleyNotFoundException(clazz, "historic", "Nenhum registro encontrado");
        }
        return historic;
    }

    @Override
    public Historic loadHistoric(final User user, final T value) {
        return this.loadHistoric(user, value.getId());
    }

    @Override
    public void checkPrecondictionDelete(final User user, final String id) {

    }

    @Override
    public void beforeDelete(final User user, final String id) {

    }

    @Override
    public void deleteById(final User user, final String id) {
        beforeDelete(user, id);
        checkPrecondictionDelete(user, id);
        if (!repository.exists(id)) {
            throw new MuttleyNotFoundException(clazz, "id", id + " este registro não foi encontrado");
        }
        this.repository.deleteById(id);
        afterDelete(user, id);
    }

    @Override
    public void afterDelete(final User user, final String id) {

    }

    @Override
    public void beforeDelete(final User user, final T value) {

    }

    @Override
    public void delete(final User user, final T value) {
        beforeDelete(user, value);
        checkPrecondictionDelete(user, value.getId());
        if (!repository.exists(value)) {
            throw new MuttleyNotFoundException(clazz, "id", value.getId() + " este registro não foi encontrado");
        }
        this.repository.delete(value);
        afterDelete(user, value);
    }

    @Override
    public void afterDelete(final User user, final T value) {

    }

    @Override
    public Long count(final User user, final Map<String, String> allRequestParams) {
        return this.repository.count(allRequestParams);
    }

    @Override
    public boolean exists(User user, T value) {
        return this.repository.exists(value);
    }

    @Override
    public boolean exists(User user, String id) {
        return this.repository.exists("_id", id);
    }

    @Override
    public List<T> findAll(final User user, final Map<String, String> allRequestParams) {
        final List<T> results = this.repository.findAll(allRequestParams);
        if (CollectionUtils.isEmpty(results)) {
            throw new MuttleyNoContentException(clazz, "user", "não foi encontrado nenhum registro");
        }
        return results;
    }

    protected Historic createHistoric(final User user) {
        final Date now = new Date();
        return new Historic()
                .setCreatedBy(user)
                .setDtCreate(now)
                .setLastChangeBy(user)
                .setDtChange(now);
    }

    protected void createMetaData(final User user, final T value) {
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

    protected Historic generateHistoricUpdate(final User user, final Historic historic) {
        return historic
                .setLastChangeBy(user)
                .setDtChange(new Date());
    }

    protected void generateMetaDataUpdate(final User user, final T value) {
        //recuperando o registro do banco
        final MetadataDocument currentMetadata = this.repository.loadMetadata(value);

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

    @Override
    public boolean isEmpty(final User user) {
        return this.count(user, null) == 0l;
    }

    @Override
    public Object getPropertyValueFromId(final User user, final String id, final String property) {
        final Map<String, Object> map = new HashMap<>(1);
        map.put("id", id);
        return this.getPropertyValueFrom(user, map, property);
    }

    @Override
    public Object getPropertyValueFrom(final User user, final Map<String, Object> condictions, final String property) {
        //Criteria condiction = Criteria.where("owner.$id").is(user.getCurrentOwner().getObjectId());
        final AggregationResults<T> results = createAggregateForLoadProperties(user, condictions, property);
        return getPropertyFrom(results.getUniqueMappedResult(), property);
    }

    @Override
    public Object[] getPropertiesValueFrom(final User user, final Map<String, Object> condictions, final String... properties) {
        //Criteria condiction = Criteria.where("owner.$id").is(user.getCurrentOwner().getObjectId());
        final AggregationResults<T> results = createAggregateForLoadProperties(user, condictions, properties);

        final T result = results.getUniqueMappedResult();

        return Stream.of(properties)
                .map(it -> getPropertyFrom(result, it))
                .toArray(Object[]::new);
    }

    protected AggregationResults<T> createAggregateForLoadProperties(final User user, final Map<String, Object> condictions, final String... properties) {
        Criteria condiction = new Criteria();
        final Set<String> keysOfCondictions = condictions.keySet();

        for (final String key : keysOfCondictions) {
            condiction = condiction.and(key).is(condictions.get(key));
        }

        return this.mongoTemplate
                .aggregate(
                        newAggregation(
                                match(condiction),
                                project(properties),
                                limit(1l)
                        ), clazz, clazz
                );
    }
}
