package br.com.muttley.model.security;

import br.com.muttley.model.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

/**
 * @author Joel Rodrigues Moreira on 17/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "#{documentNameConfig.getNameCollectionAccessPlan()}")
@CompoundIndexes({
        @CompoundIndex(name = "name_index_unique", def = "{'name' : 1}", unique = true)
})
public interface AccessPlan extends Document<ObjectId> {
    public String getName();

    public AccessPlan setName(final String name);

    public int getTotalUsers();

    public AccessPlan setTotalUsers(final int totalUsers);

    public String getDescription();

    public AccessPlan setDescription(final String description);
}
