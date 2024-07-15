package br.com.muttley.model.security.preference;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Size;

/**
 * @author Carol Cedro on 15/07/2024.
 * e-mail: <a href="mailto:ana.carolina@maxxsoft.com.br">ana.carolina@maxxsoft.com.br</a>
 */

@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "link")
public class Foto {


    @Size(max = 140, message = "A legenda pode ter no máximo 140 caracteres!")
    private String legenda;
    @URL(message = "Informe uma URL válida!")
    private String link;

    public Foto() {

    }

    @JsonCreator
    public Foto(@JsonProperty("legenda") final String legenda, @JsonProperty("link") final String link) {
        this();
        this.legenda = legenda;
        this.link = link;
    }


}
