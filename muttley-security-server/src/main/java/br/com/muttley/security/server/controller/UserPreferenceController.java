package br.com.muttley.security.server.controller;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.repository.UserPreferencesRepository;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/user-preferences", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class UserPreferenceController {

    private final UserPreferencesRepository repository;
    private final UserService userService;

    @Autowired
    public UserPreferenceController(final UserPreferencesRepository repository,
                                    final UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @RequestMapping(value = "/{idUser}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getPreferences(@PathVariable("idUser") String idUser) {
        final UserPreferences preferences = this.repository.findByUser(idUser);
        if (preferences == null) {
            throw new MuttleyNotFoundException(UserPreferences.class, "user", "Nenhuma preferencia encontrada");
        }
        return ResponseEntity.ok(preferences);
    }

    @RequestMapping(value = "/{idUser}/preferences", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity setPreference(@PathVariable("idUser") final String idUser, @RequestBody final Preference preference) {
        final UserPreferences userPreferences = (UserPreferences) getPreferences(idUser).getBody();
        if (!preference.isValid()) {
            throw new MuttleyBadRequestException(Preference.class, "key", "valor inválido");
        }
        userPreferences.set(preference);
        this.repository.save(userPreferences);
        return ResponseEntity.ok().build();

    }

    @RequestMapping(value = "/{idUser}/preferences/{key}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity removePreference(@PathVariable("idUser") final String idUser, @PathVariable("key") final String key) {
        final UserPreferences userPreferences = (UserPreferences) getPreferences(idUser).getBody();
        userPreferences.remove(key);
        this.repository.save(userPreferences);
        return ResponseEntity.ok().build();
    }
}
