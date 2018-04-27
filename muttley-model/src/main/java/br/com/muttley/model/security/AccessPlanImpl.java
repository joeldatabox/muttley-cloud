package br.com.muttley.model.security;

import br.com.muttley.model.Historic;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.Min;
import java.util.Objects;

import static br.com.muttley.model.util.ObjectIdUtils.createOf;

/**
 * @author Joel Rodrigues Moreira on 17/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
public class AccessPlanImpl implements AccessPlan {

    @Id
    private ObjectId id;
    private Historic historic;
    @NotBlank(message = "Informe um nome válido")
    private String name;
    @Min(value = 1, message = "É necessário ter ao menos 1 usuário!")
    private int totalUsers;
    private String description;

    @Override
    public ObjectId getId() {
        return this.id;
    }

    @Override
    public AccessPlanImpl setId(ObjectId id) {
        this.id = id;
        return this;
    }

    @Override
    public AccessPlanImpl setId(final String id) {
        return setId(createOf(id));
    }

    @Override
    public Historic getHistoric() {
        return this.historic;
    }

    @Override
    public AccessPlanImpl setHistoric(Historic historic) {
        this.historic = historic;
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public AccessPlanImpl setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public int getTotalUsers() {
        return this.totalUsers;
    }

    @Override
    public AccessPlanImpl setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
        return this;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public AccessPlanImpl setDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccessPlanImpl)) return false;
        AccessPlanImpl that = (AccessPlanImpl) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, 9, 63);
    }
}
