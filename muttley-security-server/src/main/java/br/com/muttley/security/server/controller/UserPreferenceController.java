package br.com.muttley.security.server.controller;

import br.com.muttley.exception.throwables.MuttleyBadRequestException;
import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.repository.UserPreferencesRepository;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/user-preferences", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class UserPreferenceController {

    private final UserService userService;

    @Autowired
    public UserPreferenceController(final UserPreferencesRepository repository,
                                    final UserService userService) {

        this.userService = userService;
    }

    @RequestMapping(value = "/{idUser}", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity getPreferences(@PathVariable("idUser") String idUser) {
        final UserPreferences preferences = this.userService.loadPreference(new User().setId(idUser));
        if (preferences == null) {
            throw new MuttleyNotFoundException(UserPreferences.class, "user", "Nenhuma preferencia encontrada");
        }
        return ResponseEntity.ok(preferences);
    }

    @RequestMapping(value = "/{idUser}/preferences", method = POST, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity setPreference(@PathVariable("idUser") final String idUser, @RequestBody final Preference preference) {
        final UserPreferences userPreferences = (UserPreferences) getPreferences(idUser).getBody();
        if (!preference.isValid()) {
            throw new MuttleyBadRequestException(Preference.class, "key", "valor inválido");
        }
        userPreferences.set(preference);
        this.userService.save(new User().setId(idUser), userPreferences);
        return ResponseEntity.ok().build();

    }

    @RequestMapping(value = "/{idUser}/preferences/{key}", method = DELETE, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity removePreference(@PathVariable("idUser") final String idUser, @PathVariable("key") final String key) {
        final UserPreferences userPreferences = (UserPreferences) getPreferences(idUser).getBody();
        userPreferences.remove(key);
        this.userService.save(new User().setId(idUser), userPreferences);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{idUser}/preferences/contains", method = GET, produces = TEXT_PLAIN_VALUE)
    public ResponseEntity containsPreferences(@PathVariable("idUser") String idUser, @RequestParam(name = "key", required = false) final String keyPreference) {
        return ResponseEntity.ok("" + this.userService.constainsPreference(new User().setId(idUser), keyPreference));
    }

    @RequestMapping(value = "/users-from-preference", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity getUsersFromPreference(@RequestParam(name = "key", required = false) final String key, @RequestParam(name = "value", required = false) final String value) {
        return ResponseEntity.ok(this.userService.getUsersFromPreference(new Preference(key, value)));
    }

    @RequestMapping(value = "/user-from-preference", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity getUserFromPreference(@RequestParam(name = "key", required = false) final String key, @RequestParam(name = "value", required = false) final String value) {
        return ResponseEntity.ok(this.userService.getUserFromPreference(new Preference(key, value)));
    }
}
