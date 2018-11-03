package br.com.muttley.security.server.service.impl;

import br.com.muttley.domain.impl.ServiceImpl;
import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.events.UserResolverEvent;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.redis.service.RedisService;
import br.com.muttley.security.server.repository.UserPreferencesRepository;
import br.com.muttley.security.server.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author Joel Rodrigues Moreira on 01/11/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Service
public class UserPreferenceServiceImpl extends ServiceImpl<UserPreferences> implements UserPreferenceService {
    private final UserPreferencesRepository repository;
    private final JwtTokenUtilService tokenService;
    private final RedisService redisService;
    private final ApplicationEventPublisher eventPublisher;
    private static final String KEY = "preferences";

    @Autowired
    public UserPreferenceServiceImpl(UserPreferencesRepository repository, final JwtTokenUtilService tokenService, final RedisService redisService, final ApplicationEventPublisher eventPublisher) {
        super(repository, UserPreferences.class);
        this.repository = repository;
        this.tokenService = tokenService;
        this.redisService = redisService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public UserPreferences save(User user, UserPreferences userPreferences) {
        final UserPreferences salvedPreferences = this.repository.save(userPreferences.setUser(user));
        updatePreferencesCache(user, salvedPreferences);
        return salvedPreferences;
    }

    @Override
    public UserPreferences getPreferences(User user) {
        final UserPreferences preferences = this.repository.findByUser(user);
        if (preferences == null) {
            throw new MuttleyNotFoundException(UserPreferences.class, "user", "Nenhuma preferencia encontrada");
        }
        return preferences;
    }

    @Override
    public UserPreferences getPreferences(final JwtToken token) {
        return getPreferences(getUserFromToken(token));
    }

    @Override
    public void setPreferences(User user, Preference preference) {
        if (!preference.isValid()) {
            throw new MuttleyBadRequestException(Preference.class, "key", "valor inválido");
        }
        final UserPreferences preferences = this.repository.save(getPreferences(user).set(preference));
        updatePreferencesCache(user, preferences);
    }

    @Override
    public void setPreferences(JwtToken token, Preference preference) {
        this.setPreferences(getUserFromToken(token), preference);
    }

    @Override
    public void removePreference(User user, String key) {
        final UserPreferences preferences = this.repository.save(getPreferences(user).remove(key));
        updatePreferencesCache(user, preferences);
    }

    @Override
    public void removePreference(JwtToken token, String key) {
        this.removePreference(getUserFromToken(token), key);
    }

    private User getUserFromToken(final JwtToken token) {
        if (token.isEmpty()) {
            throw new MuttleyBadRequestException(null, null, "informe um token válido");
        }
        final String emailFromToken = this.tokenService.getUsernameFromToken(token.getToken());

        if (isNullOrEmpty(emailFromToken)) {
            throw new MuttleyBadRequestException(null, null, "informe um token válido");
        }

        //carregando o usuário
        final UserResolverEvent userResolverEvent = new UserResolverEvent(emailFromToken);
        this.eventPublisher.publishEvent(userResolverEvent);
        return userResolverEvent.getUserResolved();
    }

    /**
     * Irá salvar ou atualizar as preferencias do usuário no cache, caso seja necessário
     */
    private void updatePreferencesCache(final User user, final UserPreferences userPreferences) {
        if (redisService.hasKey(createKey(user))) {
            final String key = createKey(user);
            redisService.set(key, userPreferences, redisService.getExpire(key));
        }
    }

    private String createKey(final String email) {
        return KEY + "-" + email;
    }

    private String createKey(final User user) {
        return createKey(user.getEmail());
    }
}
