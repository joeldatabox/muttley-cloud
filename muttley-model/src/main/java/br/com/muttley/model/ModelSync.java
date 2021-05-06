package br.com.muttley.model;

import br.com.muttley.model.security.Owner;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 05/05/2021.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface ModelSync extends Model<Owner> {

    /*default ModelSync setId(final String id) {
        setId(id);
        return this;
    }*/

    String getSync();

    ModelSync setSync(final String sync);

    Date getDtSync();

    ModelSync setDtSync(final Date date);

    @JsonIgnore
    default boolean containsSync() {
        return isEmpty(this.getSync());
    }
}