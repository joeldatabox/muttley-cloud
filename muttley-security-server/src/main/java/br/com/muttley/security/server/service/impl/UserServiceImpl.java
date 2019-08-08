package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.exception.throwables.security.MuttleySecurityBadRequestException;
import br.com.muttley.exception.throwables.security.MuttleySecurityConflictException;
import br.com.muttley.exception.throwables.security.MuttleySecurityNotFoundException;
import br.com.muttley.exception.throwables.security.MuttleySecurityUnauthorizedException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.Passwd;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.UserCreatedEvent;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.repository.UserRepository;
import br.com.muttley.security.server.service.InmutablesPreferencesService;
import br.com.muttley.security.server.service.UserPreferenceService;
import br.com.muttley.security.server.service.UserService;
import br.com.muttley.security.server.service.WorkTeamService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
@Service
public class UserServiceImpl implements UserService {

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository repository;
    private final UserPreferenceService userPreferenceService;
    private final JwtTokenUtilService tokenUtil;
    private final WorkTeamService workTeamService;
    private final InmutablesPreferencesService inmutablesPreferencesService;

    @Autowired
    public UserServiceImpl(final ApplicationEventPublisher eventPublisher,
                           final UserRepository repository,
                           final UserPreferenceService userPreferenceService,
                           final JwtTokenUtilService tokenUtil,
                           final WorkTeamService workTeamService,
                           final ObjectProvider<InmutablesPreferencesService> inmutablesPreferencesService) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
        this.userPreferenceService = userPreferenceService;
        this.tokenUtil = tokenUtil;
        this.workTeamService = workTeamService;
        this.inmutablesPreferencesService = inmutablesPreferencesService.getIfAvailable();
    }

    @Override
    public User save(final User user) {
        final User salvedUser = merge(user);
        UserPreferences preferences;
        try {
            preferences = userPreferenceService.getPreferences(user);
        } catch (MuttleyNotFoundException ex) {
            preferences = new UserPreferences();
        }
        salvedUser.setPreferences(this.userPreferenceService.save(salvedUser, preferences.setUser(salvedUser)));
        eventPublisher.publishEvent(new UserCreatedEvent(user));
        return salvedUser;
    }

    @Override
    public void save(final User user, final UserPreferences preferences) {
        preferences.setUser(user);
        this.userPreferenceService.save(user, preferences);
    }

    @Override
    public boolean remove(final User user) {
        final User other = findById(user.getId());
        repository.delete(other);
        return true;
    }

    @Override
    public boolean removeByUserName(final String userName) {
        return this.remove(findByUserName(userName));
    }

    @Override
    public User update(final User user) {
        return merge(user);
    }

    @Override
    public User updatePasswd(final Passwd passwd) {
        final User user = getUserFromToken(passwd.getToken());
        user.setPasswd(passwd);
        return save(user);
    }

    @Override
    public User findByUserName(final String userName) {
        final User user = repository.findByUserName(userName);
        if (user == null) {
            throw new MuttleySecurityNotFoundException(User.class, "userName", userName + " este registro não foi encontrado");
        }
        return user;
    }

    @Override
    public User findById(final String id) {
        final Optional<User> user = repository.findById(id);
        if (!user.isPresent()) {
            throw new MuttleySecurityNotFoundException(User.class, "id", id + " este registro não foi encontrado");
        }
        return user.get();
    }

    @Override
    public Collection<User> findAll() {
        return (Collection<User>) repository.findAll();
    }

    @Override
    public User getUserFromToken(final JwtToken token) {
        if (token != null && !token.isEmpty()) {
            final String userName = this.tokenUtil.getUsernameFromToken(token.getToken());
            if (!isNullOrEmpty(userName)) {
                final User user = findByUserName(userName);
                final UserPreferences preferences = this.userPreferenceService.getPreferences(user);
                user.setPreferences(preferences);
                user.setCurrentWorkTeam(this.workTeamService.findById(user, preferences.get(UserPreferences.WORK_TEAM_PREFERENCE).getValue().toString()));
                return user;
            }
        }
        throw new MuttleySecurityUnauthorizedException();
    }

    @Override
    public UserPreferences loadPreference(final User user) {
        return this.userPreferenceService.getPreferences(user);
    }

    private User merge(final User user) {
        if (user.getName() == null || user.getName().length() < 4) {
            throw new MuttleySecurityBadRequestException(User.class, "nome", "O campo nome deve ter de 4 a 200 caracteres!");
        }
        if (!user.isValidUserName()) {
            throw new MuttleySecurityBadRequestException(User.class, "userName", "Informe um userName válido!");
        }
        if (user.getId() == null) {
            //validando se já existe esse usuário no sistema
            try {
                if (findByUserName(user.getUserName()) != null) {
                    throw new MuttleySecurityConflictException(User.class, "userName", "UserName já cadastrado!");
                }
            } catch (MuttleySecurityNotFoundException ex) {
            }
            //validando se preencheu a senha corretamente
            if (!user.isValidPasswd()) {
                throw new MuttleySecurityBadRequestException(User.class, "passwd", "Informe uma senha válida!");
            }
            return repository.save(user);
        } else {
            final User self = findById(user.getId());

            if (!self.getUserName().equals(user.getUserName())) {
                throw new MuttleySecurityBadRequestException(User.class, "userName", "O userName não pode ser modificado!").setStatus(HttpStatus.NOT_ACCEPTABLE);
            }

            //garantindo que a senha não irá ser modificada
            user.setPasswd(self);

            return repository.save(user);
        }
    }


    public Long count(final User user, final Map<String, Object> allRequestParams) {
        //return repository.count(allRequestParams);
        throw new UnsupportedOperationException("Implemente o methodo");
    }


    public List<User> findAll(final User user, final Map<String, Object> allRequestParams) {
        throw new UnsupportedOperationException("Implemente o methodo");
        //return repository.findAll(allRequestParams);
    }

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        User user = repository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        } else {
            //verifiando se é a primeira vez que o usuário está fazendo login
            /*if (!user.isConfigured()) {
                eventPublisher.publishEvent(new FirstLoginUserEvent(user));
                //marcando como o usuário já teve um login antes
                user.setConfigured(true);
                user = update(user);
            }*/
            return new JwtUser(user);
        }
    }


}
