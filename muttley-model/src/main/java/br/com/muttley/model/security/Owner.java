package br.com.muttley.model.security;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Interface utilizada pra implementação de multi tenancy no sistema
 *
 * @author Joel Rodrigues Moreira on 29/01/18.
 * @project muttley-cloud
 */
@Document(collection = "#{documentNameConfig.getNameCollectionOwner()}")
@CompoundIndexes({
        @CompoundIndex(name = "name_userMaster_index_unique", def = "{'name' : 1, 'userMaster': 1}", unique = true)
})
public interface Owner extends br.com.muttley.model.Document<ObjectId> {
    public String getName();

    public Owner setName(final String name);

    public String getDescription();

    public Owner setDescription(final String description);

    public AccessPlan getAccessPlan();

    public Owner setAccessPlan(final AccessPlan accessPlan);
}
