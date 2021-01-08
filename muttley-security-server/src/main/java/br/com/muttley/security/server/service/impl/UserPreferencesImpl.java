package br.com.muttley.security.server.service.impl;

import br.com.muttley.model.Document;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.repository.UserPreferencesRepository;
import br.com.muttley.security.server.service.UserPreferencesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Joel Rodrigues Moreira 08/01/2021
 * <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Service
public class UserPreferencesImpl implements UserPreferencesService {
    private final UserPreferencesRepository repository;

    @Autowired
    public UserPreferencesImpl(final UserPreferencesRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(final User user, final UserPreferences preferences) {
        //devemos garantir que os
        if()
    }

    @Override
    public void setPreference(final User user, final Preference preference) {

    }

    @Override
    public void setPreference(final User user, final String key, final String value) {

    }

    @Override
    public void setPreference(final User user, final String key, final Document value) {

    }

    @Override
    public String getPreference(final User user, final String key) {
        return null;
    }

    @Override
    public UserPreferences getUserPreferences(final User user) {
        return null;
    }
}
