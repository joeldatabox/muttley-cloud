package br.com.muttley.model.security;

import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * @author Joel Rodrigues Moreira on 17/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "#{documentNameConfig.getNameCollectionAccessPlan()}")
@CompoundIndexes({
        @CompoundIndex(name = "name_index_unique", def = "{'name' : 1}", unique = true)
})
@TypeAlias("#{documentNameConfig.getNameCollectionAccessPlan()}")
@EqualsAndHashCode(of = {"id"})
@Getter
@Setter
@Accessors(chain = true)
public class AccessPlan implements Document {

    @Id
    private String id;
    private Historic historic;
    @NotBlank(message = "Informe um nome válido")
    private String name;
    @Min(value = 1, message = "É necessário ter ao menos 1 usuário!")
    private int totalUsers;
    private String description;
}
