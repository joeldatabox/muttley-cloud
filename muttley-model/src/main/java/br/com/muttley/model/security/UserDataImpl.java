package br.com.muttley.model.security;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * @author Joel Rodrigues Moreira 20/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class UserDataImpl implements UserData {
    protected String id;
    protected String name;
    protected String userName;
    protected String description;
    protected String email;
    protected Set<String> nickUsers = new HashSet<>();

    public UserDataImpl setNickUsers(final Set<String> nickUsers) {
        if (nickUsers != null) {
            this.nickUsers = nickUsers.parallelStream().map(String::toLowerCase).collect(toSet());
        }
        return this;
    }

    public UserDataImpl addNickUsers(final String nick) {
        if (nick != null) {
            this.nickUsers.add(nick.toLowerCase());
        }
        return this;
    }

    public UserDataImpl addNickUsers(final String... nick) {
        this.nickUsers.addAll(Stream.of(nick).filter(it -> it != null).map(String::toLowerCase).collect(toSet()));
        return this;
    }

}
