package br.com.muttley.model.security;

import br.com.muttley.model.security.jackson.OwnerDataDeserializerDefault;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * @author Joel Rodrigues Moreira 29/12/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 * Interface criada para Evitar o vazamente do informações  a respeito do owner para terceiros
 */
@JsonDeserialize(using = OwnerDataDeserializerDefault.class)
public interface OwnerData {
    String getId();

    String getName();

    String getDescription();

    UserData getUserMaster();
}
