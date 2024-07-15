package br.com.muttley.model.security;

import br.com.muttley.model.security.preference.Foto;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 28/12/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@JsonDeserialize(as = User.class)
public interface UserData {
    String getId();

    String getName();

    String getDescription();

    String getUserName();

    Foto getFoto();

    String getEmail();

    Set<String> getNickUsers();
}
