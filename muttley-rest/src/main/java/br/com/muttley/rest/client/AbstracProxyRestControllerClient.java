package br.com.muttley.rest.client;

import br.com.muttley.localcache.services.LocalModelService;
import br.com.muttley.model.Historic;
import br.com.muttley.model.SyncObjectId;
import br.com.muttley.rest.ModelSyncRestControllerClient;
import br.com.muttley.security.infra.resource.PageableResource;
import br.com.muttley.security.infra.service.AuthService;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 31/08/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public abstract class AbstracProxyRestControllerClient<T> implements ProxyRestControllerClient<T> {
    protected final AuthService authService;
    protected final Class<T> clazz;
    protected final ModelSyncRestControllerClient<T> client;
    protected final LocalModelService localModelService;

    public AbstracProxyRestControllerClient(final AuthService authService, final Class<T> clazz, final ModelSyncRestControllerClient<T> client, final LocalModelService localModelService) {
        this.authService = authService;
        this.clazz = clazz;
        this.client = client;
        this.localModelService = localModelService;
    }

    @Override
    public T save(final T value, final String returnEntity) {
        return this.client.save(value, returnEntity);
    }

    @Override
    public T update(final String id, final T model) {
        return this.client.update(id, model);
    }

    @Override
    public T updateBySync(final String sync, final T model) {
        return this.client.updateBySync(sync, model);
    }

    @Override
    public void synchronization(final List<T> values) {
        this.client.synchronization(values);
    }

    @Override
    public void deleteById(final String id) {
        this.client.deleteById(id);
    }

    @Override
    public void delteBySync(final String sync) {
        this.client.delteBySync(sync);
    }

    @Override
    public T findById(final String id) {
        final T value = (T) this.localModelService.loadModel(this.authService.getCurrentUser(), this.clazz, id);
        if (value == null) {
            return this.client.findById(id);
        }
        return value;
    }

    @Override
    public T findReferenceById(String id) {
        final T value = (T) this.localModelService.loadReference(this.authService.getCurrentUser(), this.clazz, id);
        if (value == null) {
            return this.client.findReferenceById(id);
        }
        return value;
    }

    @Override
    public T findBySync(final String sync) {
        final T value = (T) this.localModelService.loadModel(this.authService.getCurrentUser(), this.clazz, sync);
        if (value == null) {
            return this.client.findBySync(sync);
        }
        return value;
    }

    @Override
    public T findReferenceBySync(String sync) {
        final T value = (T) this.localModelService.loadReference(this.authService.getCurrentUser(), this.clazz, sync);
        if (value == null) {
            return this.client.findReferenceBySync(sync);
        }
        return value;
    }

    @Override
    public T findBySyncOrId(final String syncOrId) {
        final T value = (T) this.localModelService.loadModel(this.authService.getCurrentUser(), this.clazz, syncOrId);
        if (value == null) {
            return this.client.findBySyncOrId(syncOrId);
        }
        return value;
    }

    @Override
    public Set<SyncObjectId> findBySyncs(final String[] syncs) {
        return this.client.findBySyncs(syncs);
    }

    @Override
    public T first() {
        return this.client.first();
    }

    @Override
    public PageableResource list(final Map<String, String> allRequestParams) {
        return this.client.list(allRequestParams);
    }

    @Override
    public PageableResource list() {
        return this.client.list();
    }

    @Override
    public Long count(final Map<String, Object> allRequestParams) {
        return this.client.count(allRequestParams);
    }

    @Override
    public Long count() {
        return this.client.count();
    }

    @Override
    public Date getLastModify() {
        return this.client.getLastModify();
    }

    @Override
    public String getIdOfsync(final String sync) {
        return this.client.getIdOfsync(sync);
    }
}
