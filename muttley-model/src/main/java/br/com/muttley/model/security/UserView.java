package br.com.muttley.model.security;

import br.com.muttley.model.Document;
import br.com.muttley.model.MetadataDocument;
import br.com.muttley.model.security.preference.Foto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Email;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira on 29/04/19.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = {"id", "email", "userName"})
public class UserView implements UserData, Document {
    private String id;
    private String name;
    private String description;
    private String userName;
    private Foto foto;
    private String email;
    @Email(message = "Informe um email secundário válido!")
    private String emailSecundario;
    private Set<String> nickUsers;
    private boolean status;
    private String fone;
    @DBRef
    @JsonIgnore
    private Owner owner;
    private MetadataDocument metadata;

    public UserView() {
    }

    public UserView(final User user) {
        this.setId(user.getId())
                .setName(user.getName())
                .setDescription(user.getDescription())
                .setUserName(user.getUserName())
                .setFoto(user.getFoto())
                .setEmail(user.getEmail())
                .setEmailSecundario(user.getEmailSecundario())
                .setNickUsers(user.getNickUsers())
                .setFone(user.getFone())
                .setOwner(user.getCurrentOwner());
    }

    private UserView setOwner(final Passaport passaport) {
        if (passaport != null) {
            this.setOwner(passaport.getOwner());
        }
        return this;
    }

    public UserView setOwner(final Owner owner) {
        this.owner = owner;
        return this;
    }
}
