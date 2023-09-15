package br.com.muttley.model.validation;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author Joel Rodrigues Moreira on 13/01/2023.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(of = "id")
public class CollectionReferences {
    private String collection;
    private List<String> records;

    public CollectionReferences(String collection, List<String> records) {
        this.collection = collection;
        this.records = records;
    }
}
