package br.com.muttley.model.security;

import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.model.jackson.JsonHelper;
import br.com.muttley.model.security.preference.UserPreferences;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.stream.Collectors.toSet;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Created by joel on 16/01/17.
 */
@Document(collection = "#{documentNameConfig.getNameCollectionUser()}")
@CompoundIndexes({
        @CompoundIndex(name = "userName_index_unique", def = "{'userName' : 1}", unique = true),
        @CompoundIndex(name = "email_index", def = "{'email' : 1}"),
        @CompoundIndex(name = "nickUsers_index", def = "{'nickUsers' : 1}")
})
public class User implements Serializable {
    @Transient
    @JsonIgnore
    private static final int SALT = 8;
    @Transient
    @JsonIgnore
    private static final String EMAIL_PATTERN = "\\b(^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";

    @Id
    private String id;
    @Transient
    private Set<WorkTeam> workTeams;//os workteam devem ser carregados separadamente
    @Transient
    private WorkTeam currentWorkTeam;
    @NotBlank(message = "O campo nome não pode ser nulo!")
    @Size(min = 4, max = 200, message = "O campo nome deve ter de 4 a 200 caracteres!")
    private String name;
    private String description;
    @Email(message = "Informe um email válido!")
    private String email;
    @NotBlank(message = "Informe um userName válido")
    private String userName;
    private Set<String> nickUsers = new HashSet<>();
    @NotBlank(message = "Informe uma senha valida!")
    private String passwd;
    private Date lastPasswordResetDate;
    private Boolean enable;
    @Transient
    private Set<Authority> authorities;//Os authorities devem ser repassado pelo workteam corrente
    @Transient
    private UserPreferences preferences;
    //Define se o usuário é do odin ou de algum outro owner
    private boolean odinUser = false;

    public User() {
        this.authorities = new LinkedHashSet();
        this.enable = true;
        this.lastPasswordResetDate = Date.from(Instant.now());
        this.workTeams = new HashSet();
    }

    @JsonCreator
    public User(
            @JsonProperty("id") final String id,
            @JsonProperty("workTeams") final Set<WorkTeam> workTeams,
            @JsonProperty("currentWorkTeam") final WorkTeam currentWorkTeam,
            @JsonProperty("name") final String name,
            @JsonProperty("description") final String description,
            @JsonProperty("userName") final String userName,
            @JsonProperty("email") final String email,
            @JsonProperty("nickUsers") final Set<String> nickUsers,
            @JsonProperty("passwd") final String passwd,
            @JsonProperty("lastPasswordResetDate") final Date lastPasswordResetDate,
            @JsonProperty("enable") final Boolean enable,
            @JsonProperty("authorities") final Set<Authority> authorities,
            @JsonProperty("preferences") final UserPreferences preferences) {
        this.id = id;
        this.workTeams = workTeams;
        this.currentWorkTeam = currentWorkTeam;
        this.name = name;
        this.description = description;
        this.userName = userName;
        this.email = email;
        this.setNickUsers(nickUsers);
        this.passwd = passwd;
        this.lastPasswordResetDate = lastPasswordResetDate;
        this.enable = enable;
        this.authorities = authorities;
        this.preferences = preferences;
    }

    public User(final UserPayLoad payLoad) {
        this();
        this.setName(payLoad.getName());
        this.setDescription(payLoad.getDescription());
        this.setUserName(payLoad.getUserName());
        this.setEmail(payLoad.getEmail());
        this.setNickUsers(payLoad.getNickUsers());
        this.setPasswd(payLoad.getPasswd());
    }

    public String getId() {
        return id;
    }

    public User setId(final String id) {
        this.id = id;
        return this;
    }

    public Set<WorkTeam> getWorkTeams() {
        return workTeams;
    }

    public User setWorkTeams(final Collection<? extends WorkTeam> workTeams) {
        this.workTeams = new HashSet<>(workTeams);
        return this;
    }

    public User addWorkTeam(final Collection<WorkTeam> workTeams) {
        this.workTeams.addAll(workTeams);
        return this;
    }

    public User addWorkTeam(final WorkTeam... workTeams) {
        for (WorkTeam work : workTeams) {
            this.workTeams.add(work);
        }
        return this;
    }

    @JsonIgnore
    public Owner getCurrentOwner() {
        if (isNull(getCurrentWorkTeam())) {
            return null;
        }
        return getCurrentWorkTeam().getOwner();
    }

    public WorkTeam getCurrentWorkTeam() {
        return currentWorkTeam;
    }

    public User setCurrentWorkTeam(final WorkTeam currentWorkTeam) {
        this.currentWorkTeam = currentWorkTeam;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(final String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public User setDescription(final String description) {
        this.description = description;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public User setUserName(final String userName) {
        this.userName = userName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public User setEmail(final String email) {
        this.email = email != null ? email.toLowerCase() : email;
        return this;
    }

    public Set<String> getNickUsers() {
        return nickUsers;
    }

    public User setNickUsers(final Set<String> nickUsers) {
        if (nickUsers != null) {
            this.nickUsers = nickUsers.parallelStream().map(String::toLowerCase).collect(toSet());
        }
        return this;
    }

    public User addNickUsers(final String nick) {
        if (nick != null) {
            this.nickUsers.add(nick.toLowerCase());
        }
        return this;
    }

    public User addNickUsers(final String... nick) {
        this.nickUsers.addAll(Stream.of(nick).filter(it -> it != null).map(String::toLowerCase).collect(toSet()));
        return this;
    }

    @JsonIgnore
    public String getPasswd() {
        return passwd;
    }

    @JsonProperty
    public User setPasswd(final String passwd) {
        if (passwd == null || passwd.trim().isEmpty()) {
            throw new MuttleySecurityBadRequestException(User.class, "passwd", "Informe uma senha valida!");
        }
        this.passwd = new BCryptPasswordEncoder(SALT).encode(passwd);
        return this;
    }

    public User setPasswd(final User user) {
        this.passwd = user.passwd;
        return this;
    }

    public User setPasswd(final Passwd passwd) {
        if (!checkPasswd(passwd.getActualPasswd())) {
            throw new MuttleySecurityBadRequestException(User.class, "passwd", "A senha atual informada é invalida!").setStatus(HttpStatus.NOT_ACCEPTABLE);
        }
        this.passwd = new BCryptPasswordEncoder(SALT).encode(passwd.getNewPasswd());
        return this;
    }

    public boolean checkPasswd(final String passwd) {
        return checkPasswd(passwd, this.passwd);
    }

    public boolean checkPasswd(final String passwd, String cryptPasswd) {
        return new BCryptPasswordEncoder().matches(passwd, cryptPasswd);
    }

    @JsonIgnore
    public boolean isValidPasswd() {
        return this.passwd != null && !this.passwd.isEmpty();
    }

    public Boolean isEnable() {
        return enable;
    }

    public User setEnable(final Boolean enable) {
        this.enable = enable;
        return this;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }

    @JsonProperty
    public User setAuthorities(final Set<Authority> authorities) {
        authorities.forEach(a -> checkAuthority(a));
        this.authorities = authorities;
        return this;
    }

    public User setAuthorities(final Collection<Role> roles) {
        this.authorities = roles.parallelStream().map(it -> new AuthorityImpl(it)).collect(toSet());
        return this;
    }

    public User addAuthority(final Authority authority) {
        checkAuthority(authority);
        this.authorities.add(authority);
        return this;
    }

    public User addAuthorities(final Collection<Authority> authorities) {
        authorities.forEach(a -> checkAuthority(a));
        this.authorities.addAll(authorities);
        return this;
    }

    public Date getLastPasswordResetDate() {
        return lastPasswordResetDate;
    }

    public User setLastPasswordResetDate(final Date lastPasswordResetDate) {
        this.lastPasswordResetDate = lastPasswordResetDate;
        return this;
    }

    public final boolean inRole(final String role) {
        try {
            return role == null ? false : inRole(new AuthorityImpl(role));
        } catch (IllegalArgumentException iex) {
            return false;
        }
    }

    public final boolean inRole(final Authority role) {
        return getAuthorities().contains(role);
    }

    public final boolean inAnyRole(final String... roles) {
        return inAnyRole(Stream.of(roles).map(AuthorityImpl::new));
    }

    public final boolean inAnyRole(final Role... roles) {
        return this.inAnyRole(Stream.of(roles).map(AuthorityImpl::new));
    }

    public final boolean inAnyRole(final Authority... roles) {
        return inAnyRole(Stream.of(roles));
    }

    public final boolean inAnyRole(final Stream<Authority> roles) {
        return roles.filter(it -> it != null).anyMatch(getAuthorities()::contains);
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public User setPreferences(final UserPreferences preferences) {
        this.preferences = preferences;
        return this;
    }

    public boolean isOdinUser() {
        return odinUser;
    }

    public User setOdinUser(final boolean odinUser) {
        this.odinUser = odinUser;
        return this;
    }

    @JsonIgnore
    public boolean containsPreference(final String keyPreference) {
        return this.preferences.contains(keyPreference);
    }

    @JsonIgnore
    public boolean preferencesIsEmpty() {
        return this.preferences != null ? this.preferences.isEmpty() : true;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        final User user = (User) o;
        return Objects.equals(getId(), user.getId()) && Objects.equals(getEmail(), user.getEmail()) && Objects.equals(getUserName(), user.getUserName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getEmail(), getUserName());
    }

    public String toJson() {
        return JsonHelper.toJson(this);
    }

    @JsonIgnore
    private boolean isValidEmail() {
        return User.isValidEmail(this.email);
    }

    @JsonIgnore
    public static boolean isValidEmail(final String email) {
        if ((email == null) || (email.trim().isEmpty()))
            return false;
        final Pattern pattern = Pattern.compile(EMAIL_PATTERN, CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidUserName() {
        return isValidUserName(this.userName);
    }

    /**
     * realiza validações basicar para o email, userName e nickNames
     */
    public void validateBasicInfoForLogin() {
        if (!isEmpty(this.email)) {
            if (!this.isValidEmail()) {
                throw new MuttleySecurityBadRequestException(User.class, "email", "Informe um email válido!");
            }
        }
        if (!CollectionUtils.isEmpty(this.nickUsers)) {

            this.nickUsers.forEach(it -> {
                if (!isValidUserName(it)) {
                    new MuttleySecurityBadRequestException(User.class, "nickUsers", "Informe nickUser válidos")
                            .addDetails("informed", this.nickUsers);
                }
            });
        }
        if (!isValidUserName(this.userName)) {
            new MuttleySecurityBadRequestException(User.class, "nickUsers", "Informe nickUser válidos")
                    .addDetails("informed", this.nickUsers);
        }
    }

    /**
     * Para evitar bugs com o Mongodb, é necessário garantirmos que não sera salvo um enum como Authority
     */
    private void checkAuthority(Authority authority) {
        if (authority instanceof Enum) {
            throw new IllegalArgumentException("Um authority não pode ser instancia de Enum: [" + authority.toString() + "]");
        }
    }

    public static boolean isValidUserName(final String userName) {
        return UserNameValidator.isValid(4, 70, userName);
    }

    private static final class UserNameValidator {
        //private static final char[] SUPPORT_SYMBOLS_CHAR = {'.', '_', '-'};
        private static final char[] SUPPORT_SYMBOLS_CHAR = {'_', '-'};

        public static boolean isValid(final int minLength, final int maxLength, final String userName) {

            // check empty
            if (userName == null || userName.length() == 0) {
                return false;
            }

            // check length
            if (userName.length() < minLength || userName.length() > maxLength) {
                return false;
            }

            return isValidUsername(userName.toCharArray());
        }

        private static boolean isValidUsername(final char[] userName) {

            int currentPosition = 0;
            boolean valid = true;

            // check char by char
            for (char c : userName) {

                // if alphanumeric char, no need check, process next
                if (!Character.isLetterOrDigit(c)) {

                    // for non-alphanumeric char, also not a supported symbol, break
                    if (!isSupportedSymbols(c)) {
                        valid = false;
                        break;
                    }

                    // ensures first and last char not a supported symbol
                    if (userName[0] == c || userName[userName.length - 1] == c) {
                        valid = false;
                        break;
                    }

                    // ensure supported symbol does not appear consecutively
                    // is next position also a supported symbol?
                    if (isSupportedSymbols(userName[currentPosition + 1])) {
                        valid = false;
                        break;
                    }

                }

                currentPosition++;
            }

            return valid;

        }

        private static boolean isSupportedSymbols(final char symbol) {
            for (char temp : SUPPORT_SYMBOLS_CHAR) {
                if (temp == symbol) {
                    return true;
                }
            }
            return false;
        }
    }
}
