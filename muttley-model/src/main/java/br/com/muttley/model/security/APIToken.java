package br.com.muttley.model.security;

import br.com.muttley.model.Document;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.security.jackson.UserDeserializer;
import br.com.muttley.model.security.jackson.UserSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author Joel Rodrigues Moreira on 08/08/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class APIToken implements Document {
    @Id
    private String id;
    @DBRef
    private Owner owner;
    @NotNull(message = "É nécessário ter um usuário master no grupo de trabalho")
    @JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)
    @DBRef
    private User user;
    private Date dtCreate;
    private String version;
    private String description;
    private MetadataDocument metadata;

}
