package br.com.muttley.model.security;

import br.com.muttley.model.Document;
import br.com.muttley.model.Historic;
import br.com.muttley.model.jackson.converter.DocumentSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 15/07/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@org.springframework.data.mongodb.core.mapping.Document(collection = "view_muttley_users")
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class UserView implements Document {
    private String id;
    private String name;
    private String email;
    @DBRef
    @JsonSerialize(using = DocumentSerializer.class)
    private Set<Owner> owners;
    private Historic historic;

}

