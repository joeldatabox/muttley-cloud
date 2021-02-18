package br.com.muttley.model.security;

import br.com.muttley.metadata.anotations.SensitiveNavigation;
import br.com.muttley.model.Historic;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.jackson.converter.DocumentSerializer;
import br.com.muttley.model.security.jackson.AccessPlanDeserializer;
import br.com.muttley.model.security.jackson.UserDeserializer;
import br.com.muttley.model.security.jackson.UserSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = "#{@documentNameConfig.getNameCollectionOwner()}")
@CompoundIndexes({
        @CompoundIndex(name = "name_userMaster_index_unique", def = "{'name' : 1, 'userMaster': 1}", unique = true)
})
@TypeAlias("muttley-owners")
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
@SensitiveNavigation
public class Owner implements br.com.muttley.model.Document {
    @Id
    protected String id;
    @Indexed
    protected String name;
    protected String description;
    @NotNull(message = "Informe o usuário master")
    @JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)
    @DBRef
    protected User userMaster;
    @JsonSerialize(using = DocumentSerializer.class)
    @JsonDeserialize(using = AccessPlanDeserializer.class)
    @DBRef
    protected AccessPlan accessPlan;
    protected Historic historic;
    protected MetadataDocument metadata;
}
