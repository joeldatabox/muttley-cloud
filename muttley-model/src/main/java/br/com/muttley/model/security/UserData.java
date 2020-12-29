package br.com.muttley.model.security;

import java.util.Set;

/**
 * @author Joel Rodrigues Moreira 28/12/2020
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public interface UserData {
    String getId();

    String getName();

    String getDescription();

    String getUserName();

    String getEmail();

    Set<String> getNickUsers();
}
