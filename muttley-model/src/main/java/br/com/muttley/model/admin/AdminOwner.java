package br.com.muttley.model.admin;

import br.com.muttley.annotations.index.CompoundIndexes;
import br.com.muttley.model.security.Owner;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Joel Rodrigues Moreira 20/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = "#{documentNameConfig.getNameCollectionAdminOwner()}")
@CompoundIndexes({
        @CompoundIndex(name = "userMaster_index_unique", def = "{'userMaster': 1}", unique = true)
})
public class AdminOwner extends Owner {
}
