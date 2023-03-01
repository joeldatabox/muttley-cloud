package br.com.muttley.model.workteam;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.jackson.UserDeserializer;
import br.com.muttley.model.security.jackson.UserSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bson.types.ObjectId;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.muttley.model.security.domain.Domain.PUBLIC;
import static br.com.muttley.model.security.domain.Domain.RESTRICTED;

/**
 * @author Joel Rodrigues Moreira on 08/03/2022.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Getter
@Setter
@Accessors(chain = true)
public class WorkTeamDomain {
    /*@JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)*/
    private User userMaster;
    /*@JsonSerialize(using = UserCollectionSerializer.class)
    @JsonDeserialize(using = UserSetDeserializer.class)*/
    private Set<WorkTeamMember> supervisors;
    private Set<WorkTeamMember> colleagues;
    private Set<WorkTeamMember> subordinates;

    public WorkTeamDomain() {
        this.supervisors = new HashSet<>();
        this.colleagues = new HashSet<>();
        this.subordinates = new HashSet<>();
    }

    public Set<User> getAllUsers() {
        final Set<User> users = Arrays.asList(
                        this.supervisors,
                        this.colleagues,
                        this.subordinates
                ).parallelStream()
                .filter(Objects::nonNull)
                .map(it ->
                        it.parallelStream()
                                .map(WorkTeamMember::getUser)
                                .collect(Collectors.toSet())
                ).reduce((acc, item) -> {
                    acc.addAll(item);
                    return acc;
                }).orElseGet(HashSet::new);

        if (userMaster != null) {
            users.add(userMaster);
        }
        return users;
    }

    public Set<ObjectId> getIdsOfAllUsers() {
        return this.getAllUsers()
                .parallelStream()
                .map(it -> it.getObjectId())
                .collect(Collectors.toSet());
    }

    public WorkTeamDomain addSupervisors(final WorkTeamMember workTeamMember) {
        this.supervisors.add(workTeamMember);
        return this;
    }

    public WorkTeamDomain addSupervisors(final User user) {
        this.supervisors.add(new WorkTeamMember(user, PUBLIC));
        return this;
    }

    public WorkTeamDomain addColleagues(final WorkTeamMember workTeamMember) {
        this.colleagues.add(workTeamMember);
        return this;
    }

    public WorkTeamDomain addColleagues(final User user) {
        this.colleagues.add(new WorkTeamMember(user, RESTRICTED));
        return this;
    }

    public WorkTeamDomain addSubordinates(final WorkTeamMember workTeamMember) {
        this.subordinates.add(workTeamMember);
        return this;
    }

    public WorkTeamDomain addSubordinates(final User user) {
        this.subordinates.add(new WorkTeamMember(user, null));
        return this;
    }
}
