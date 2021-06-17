package br.com.muttley.domain.service.impl;

import br.com.muttley.domain.service.ModelSyncService;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyConflictException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.ModelSync;
import br.com.muttley.model.SyncObjectId;
import br.com.muttley.model.TimeZoneDocument;
import br.com.muttley.model.VersionDocument;
import br.com.muttley.model.security.User;
import br.com.muttley.mongo.service.repository.CustomMongoRepository;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class ModelSyncServiceImpl<T extends ModelSync> extends ModelServiceImpl<T> implements ModelSyncService<T> {

    protected final MongoTemplate mongoTemplate;
    protected final CustomMongoRepository<T> repository;

    @Autowired
    public ModelSyncServiceImpl(final CustomMongoRepository<T> repository, final Class<T> clazz, final MongoTemplate mongoTemplate) {
        super(repository, mongoTemplate, clazz);
        this.mongoTemplate = mongoTemplate;
        this.repository = repository;
    }


    @Override
    public T save(final User user, final T value) {
        //validando o sync
        this.checkSyncIndex(user, value);
        this.checkDtSync(user, value);
        return super.save(user, value);
    }

    @Override
    public T update(final User user, final T value) {
        //devemos garantir que ninguem alterou o sync do registro
        final T other = findById(user, value.getId());
        //garantindo o sync do registro
        value.setSync(other.getSync());
        this.checkDtSync(user, value);
        return super.update(user, value);
    }

    @Override
    public void synchronize(final User user, final Collection<T> records) {
        if (records.size() > 100) {
            throw new MuttleyBadRequestException(this.clazz, null, "Cada requisiçao pode ter no maximo 100 registros");
        }
        //quebrando em pacotes de 50 registros
        Lists.partition(records instanceof List ? (List<T>) records : new ArrayList<>(records), 50)
                .stream()
                //processando para carregar os respectivos ids caso os registros não tenha id
                .map(subList -> {
                    this.getIdsOfSyncs(user, subList.parallelStream()
                            //removendo os itens que já tem id
                            .filter(it -> StringUtils.isEmpty(it.getId()))
                            //pegando apenas os ids para busca
                            .map(ModelSync::getSync)
                            .collect(Collectors.toSet())
                    ).parallelStream()
                            //com base no syncs carregado, vamos interar a lista e preencher nos objtos que não tem sync
                            .forEach((final SyncObjectId syncId) -> {
                                subList.parallelStream().filter(it -> syncId.getSync().equals(it.getSync())).forEach(it -> it.setId(syncId.getId()));
                            });
                    return subList;
                }).forEach(subList -> {
            subList.parallelStream()
                    .peek(it -> this.checkDtSync(user, it))
                    //agrupando os dados em registros que possui id ou não
                    .collect(Collectors.groupingBy(ModelSync::contaisObjectId))
                    //interando o agrupamento
                    .entrySet()
                    .forEach((key) -> {
                        if (key.getKey()) {
                            //update em cascata
                            update(user, key.getValue());
                        } else {
                            //insere em cascata
                            saveOnly(user, key.getValue());
                        }
                    });
            ;
        });/*


        Lists.partition(records instanceof List ? (List<T>) records : new ArrayList<>(records), 50);

        patials.stream()
                .forEach(it -> {
                    this.getIdsOfSyncs(user, it.stream().filter(iit -> StringUtils.isEmpty(iit.getId())).map(iit -> iit.getSync()).collect(Collectors.toSet()))
                            .forEach(iit -> {
                                it.stream().filter(iiit -> iit.getSync().equals(iiit.getSync())).forEach(iiit ->);
                            });
                });*//*
        records.stream()
                .map((final T value) -> {
                    if (StringUtils.isEmpty(value.getId())) {
                        value.setId(this.getIdOfSync(user, value.getSync()));
                    }
                    return value;
                })
                .forEach((final T value) -> {
                    if (value.getId() == null) {
                        this.save(user, value);
                    } else {
                        this.update(user, value);
                    }
                });*/
    }

    @Override
    public void checkSyncIndex(final User user, final T value) {
        if (!StringUtils.isEmpty(value.getSync())) {
            //devemos garantir se já não existe o determinado sync
            if (this.existSync(user, value)) {
                throw new MuttleyConflictException(clazz, "sync", "Já existe um registro com esse sync");
            }
        } else {
            //Se não foi passado nenhum syn vamos gerar algum
            value.setSync("agrifocus-" + new ObjectId(new Date()).toString());
        }
    }

    @Override
    public T updateBySync(final User user, final T value) {
        //devemos garantir que ninguem irá altera o ObjectId do registro
        final T other = findBySync(user, value.getSync());
        //garantindo o id do registro
        value.setId(other.getId());
        return update(user, value);
    }

    @Override
    public T findBySync(final User user, final String sync) {
        T value = this.mongoTemplate
                .findOne(
                        new Query(
                                where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                        .and("sync").is(sync)
                        ), clazz);
        if (value == null) {
            throw new MuttleyNotFoundException(clazz, "sync", "Registro não encontrado!")
                    .addDetails("syncInformado", sync);
        }
        return value;
    }

    @Override
    public T findByIdOrSync(final User user, final String idSync) {
        if (ObjectId.isValid(idSync)) {
            return findById(user, idSync);
        } else {
            return findBySync(user, idSync);
        }
    }

    @Override
    public Date getLastModify(final User user) {
        final AggregationResults results = this.mongoTemplate
                .aggregate(
                        newAggregation(
                                match(where("owner.$id").is(user.getCurrentOwner().getObjectId())),
                                project().and("$historic.dtChange").as("dtChange"),
                                sort(DESC, "dtChange"),
                                limit(1)
                        ), clazz, Historic.class
                );
        return results.getUniqueMappedResult() != null ? ((Historic) results.getUniqueMappedResult()).getDtChange() : null;
    }

    @Override
    public void deleteById(final User user, final String id) {
        super.deleteById(user, id);
    }

    @Override
    public void delete(final User user, final T value) {
        super.delete(user, value);
    }

    @Override
    protected void createMetaData(final User user, final T value) {
        final TimeZoneDocument timeZoneDocument = currentTimezone.getCurrentTimezoneDocument();
        if (StringUtils.isEmpty(timeZoneDocument.getCurrentTimeZone())) {
            timeZoneDocument.setCurrentTimeZone(timeZoneDocument.getServerCurrentTimeZone());
        }
        if (StringUtils.isEmpty(timeZoneDocument.getCreateTimeZone())) {
            timeZoneDocument.setCreateTimeZone(timeZoneDocument.getServerCreteTimeZone());
        }

        //se não tiver nenhum metadata criado, vamos criar um
        if (!value.containsMetadata()) {
            value.setMetadata(
                    new MetadataDocument()
                            .setTimeZones(timeZoneDocument)
                            .setVersionDocument(
                                    new VersionDocument()
                                            .setOriginVersionClientCreate(currentVersion.getCurrentValue())
                                            .setOriginVersionClientLastUpdate(currentVersion.getCurrentValue())
                                            .setOriginNameClientCreate(userAgentName.getCurrentValue())
                                            .setOriginNameClientLastUpdate(userAgentName.getCurrentValue())
                                            .setServerVersionCreate(currentVersion.getCurrenteFromServer())
                                            .setServerVersionLastUpdate(currentVersion.getCurrenteFromServer())
                            )
            );
        } else {
            //se não tem um timezone válido, vamos criar um
            if (!value.getMetadata().containsTimeZones()) {
                /*final TimeZoneDocument timeZoneDocument = currentTimezone.getCurrentTimezoneDocument();
                if (StringUtils.isEmpty(timeZoneDocument.getCurrentTimeZone())) {
                    timeZoneDocument.setCurrentTimeZone(timeZoneDocument.getServerCurrentTimeZone());
                }
                if (StringUtils.isEmpty(timeZoneDocument.getCreateTimeZone())) {
                    timeZoneDocument.setCreateTimeZone(timeZoneDocument.getServerCreteTimeZone());
                }*/
                value.getMetadata().setTimeZones(timeZoneDocument);
            } else {
                //se chegou aqui é sinal que já possui infos de timezones e devemos apenas checar e atualizar caso necessário

                //O timezone atual informado é valido?
                if (value.getMetadata().getTimeZones().isValidCurrentTimeZone()) {
                    //adicionado a mesma info no createTimezone já que estamos criando um novo registro
                    value.getMetadata().getTimeZones().setCreateTimeZone(value.getMetadata().getTimeZones().getCurrentTimeZone());
                    value.getMetadata().getTimeZones().setCurrentTimeZone(value.getMetadata().getTimeZones().getCurrentTimeZone());
                } else {
                    //setando um timezone padrão para não dar prego na lógica interna do serviço
                    value.getMetadata().getTimeZones().setCreateTimeZone(currentTimezone.getCurrenteTimeZoneFromServer());
                    value.getMetadata().getTimeZones().setCurrentTimeZone(currentTimezone.getCurrenteTimeZoneFromServer());
                }

                //adicionando infos de timezone do servidor
                final String currentServerTimezone = currentTimezone.getCurrenteTimeZoneFromServer();
                value.getMetadata().getTimeZones().setServerCreteTimeZone(currentServerTimezone);
                value.getMetadata().getTimeZones().setServerCurrentTimeZone(currentServerTimezone);
            }

            //criando version valido
            value.getMetadata()
                    .setVersionDocument(
                            new VersionDocument()
                                    .setOriginVersionClientCreate(currentVersion.getCurrentValue())
                                    .setOriginVersionClientLastUpdate(currentVersion.getCurrentValue())
                                    .setOriginNameClientCreate(userAgentName.getCurrentValue())
                                    .setOriginNameClientLastUpdate(userAgentName.getCurrentValue())
                                    .setServerVersionCreate(currentVersion.getCurrenteFromServer())
                                    .setServerVersionLastUpdate(currentVersion.getCurrenteFromServer())
                    );


        }
    }

    @Override
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


        if (StringUtils.isEmpty(currentMetadata.getTimeZones().getCurrentTimeZone())) {
            //setando um timezone padrão para não dar prego na lógica interna do serviço
            currentMetadata.getTimeZones().setCurrentTimeZone(currentTimezone.getCurrenteTimeZoneFromServer());
        }

        value.setMetadata(currentMetadata);
    }

    @Override
    public void deleteBySync(final User user, final String sync) {
        final T value = findBySync(user, sync);
        this.delete(user, value);
    }

    @Override
    public boolean existSync(final User user, final T value) {
        return this.mongoTemplate.exists(
                new Query(
                        where("owner.$id").is(user.getCurrentOwner().getObjectId()).and("sync").is(value.getSync())
                ), clazz
        );
    }

    @Override
    public String getIdOfSync(final User user, final String sync) {
        final Map<String, Object> map = new HashMap<>();
        map.put("sync", sync);
        return (String) this.getPropertyValueFrom(user, map, "id");
        /*final AggregationResults results = this.mongoTemplate
                .aggregate(
                        newAggregation(
                                match(
                                        where("owner.$id").is(user.getCurrentOwner().getObjectId())
                                                .and("sync").is(sync)
                                ),
                                project("id"),
                                limit(1)
                        ), clazz, clazz
                );
        return results.getUniqueMappedResult() != null ? ((T) results.getUniqueMappedResult()).getId() : null;*/
    }

    @Override
    public Set<SyncObjectId> getIdsOfSyncs(final User user, final Set<String> syncs) {
        if (CollectionUtils.isEmpty(syncs)) {
            return Collections.emptySet();
        }

        final AggregationResults<SyncObjectId> results = this.mongoTemplate.aggregate(
                newAggregation(
                        match(where("owner.$id").is(user.getCurrentOwner().getObjectId()).and("sync").in(syncs)),
                        project("id", "sync")
                )
                , clazz, SyncObjectId.class);

        if (results == null || CollectionUtils.isEmpty(results.getMappedResults())) {
            return Collections.emptySet();
        }
        return new HashSet<>(results.getMappedResults());
    }

    protected void checkDtSync(final User user, final T value) {
        if (value.getDtSync() == null) {
            value.setDtSync(new Date());
        }
    }
}
