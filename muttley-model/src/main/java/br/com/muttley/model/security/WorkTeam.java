package br.com.muttley.model.security;

import br.com.muttley.model.Document;
import br.com.muttley.model.jackson.converter.DocumentSerializer;
import br.com.muttley.model.security.jackson.OwnerDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 28/02/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * Representa o time de trabalho ou um grupo de usuário
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "#{documentNameConfig.getNameCollectionWorkTeam()}")
@CompoundIndexes({
        @CompoundIndex(name = "name_userMaster_index_unique", def = "{'name' : 1, 'userMaster': 1}", unique = true)
})
public interface WorkTeam extends Document<ObjectId> {

    String getName();

    String getDescription();

    User getUserMaster();

    Owner getOwner();

    Set<User> getMembers();

    Set<Authority> getAuthorities();

}
