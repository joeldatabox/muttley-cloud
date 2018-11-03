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
import br.com.muttley.security.server.service.UserPreferenceService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    @Autowired
    public UserServiceImpl(final ApplicationEventPublisher eventPublisher,
                           final UserRepository repository,
                           final UserPreferenceService userPreferenceService,
                           final JwtTokenUtilService tokenUtil) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
        this.userPreferenceService = userPreferenceService;
        this.tokenUtil = tokenUtil;
    }

    @Override
    public User save(final User user) {
        UserPreferences preferences;
        try {
            preferences = userPreferenceService.getPreferences(user);
        } catch (MuttleyNotFoundException ex) {
            preferences = new UserPreferences();
        }
        final User salvedUser = merge(user);
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
    public boolean removeByEmail(final String email) {
        return this.remove(findByEmail(email));
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
    public User findByEmail(final String email) {
        final User user = repository.findByEmail(email);
        if (user == null) {
            throw new MuttleySecurityNotFoundException(User.class, "email", email + " este registro não foi encontrado");
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
                return findByEmail(userName);
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
        if (!user.isValidEmail()) {
            throw new MuttleySecurityBadRequestException(User.class, "email", "Informe um email válido!");
        }
        if (user.getId() == null) {
            //validando se já existe esse usuário no sistema
            try {
                if (findByEmail(user.getEmail()) != null) {
                    throw new MuttleySecurityConflictException(User.class, "email", "Email já cadastrado!");
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

            if (!self.getEmail().equals(user.getEmail())) {
                throw new MuttleySecurityBadRequestException(User.class, "email", "O email não pode ser modificado!").setStatus(HttpStatus.NOT_ACCEPTABLE);
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
        User user = repository.findByEmail(username);
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
