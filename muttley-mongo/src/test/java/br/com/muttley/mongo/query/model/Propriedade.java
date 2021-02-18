package br.com.muttley.mongo.query.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Joel Rodrigues Moreira on 29/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document(collection = "propriedade")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Propriedade {
    @Id
    private String id;
    private String descricao;
    @DBRef
    private Cor cor;
}
