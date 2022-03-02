package br.com.muttley.model.admin;

import br.com.muttley.model.security.Passaport;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import static br.com.muttley.model.admin.AdminPassaport.TYPE_ALIAS;

/**
 * @author Joel Rodrigues Moreira 20/04/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "#{documentNameConfig.getNameCollectionAdminPassaport()}")
@CompoundIndexes({
        @CompoundIndex(name = "name_userMaster_index_unique", def = "{'name' : 1, 'userMaster': 1}", unique = true)
})
@TypeAlias(TYPE_ALIAS)
public class AdminPassaport extends Passaport {

    @Transient
    @JsonIgnore
    public static final String TYPE_ALIAS = "admin-work-team";
}
