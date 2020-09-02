package br.com.muttley.mongo.query.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Joel Rodrigues Moreira on 29/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Document("pessoa")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Cor {
    private String id;
    private String nome;
    private String descricao;
}
