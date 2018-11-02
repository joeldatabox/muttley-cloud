package br.com.muttley.security.server.service.impl;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.repository.UserPreferencesRepository;
import br.com.muttley.security.server.service.UserPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira on 01/11/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Service
public class UserPreferenceServiceImpl implements UserPreferenceService {
    private final UserPreferencesRepository repository;

    @Autowired
    public UserPreferenceServiceImpl(UserPreferencesRepository repository) {
        this.repository = repository;
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
    public void setPreferences(User user, Preference preference) {
        if (!preference.isValid()) {
            throw new MuttleyBadRequestException(Preference.class, "key", "valor inválido");
        }
        this.repository.save(getPreferences(user).set(preference));
    }

    @Override
    public void removePreference(User user, String key) {
        this.repository.save(getPreferences(user).remove(key));
    }
}
