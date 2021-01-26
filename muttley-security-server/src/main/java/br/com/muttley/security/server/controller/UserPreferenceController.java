package br.com.muttley.security.server.controller;

import br.com.muttley.exception.throwables.MuttleyNotFoundException;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.server.service.AuthService;
import br.com.muttley.security.server.service.UserPreferencesService;
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
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Joel Rodrigues Moreira on 24/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@RestController
@RequestMapping(value = "/api/v1/user-preferences", produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
public class UserPreferenceController {

    private final UserPreferencesService preferencesService;
    private final AuthService authService;
    private final UserService userService;

    @Autowired
    public UserPreferenceController(final UserPreferencesService preferencesService, final AuthService authService, final UserService userService) {
        this.preferencesService = preferencesService;
        this.authService = authService;
        this.userService = userService;
    }

    @RequestMapping(method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity getUserPreferences() {
        final UserPreferences preferences = this.preferencesService.getUserPreferences(this.authService.getCurrentUser());
        if (preferences == null) {
            throw new MuttleyNotFoundException(UserPreferences.class, "user", "Nenhuma preferencia encontrada");
        }
        return ResponseEntity.ok(preferences);
    }

    @RequestMapping(value = "/preferences", method = PUT, consumes = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity setPreference(@RequestBody final Preference preference) {
        this.preferencesService.setPreference(this.authService.getCurrentUser(), preference);
        return ResponseEntity.ok().build();

    }

    @RequestMapping(value = "/preferences/{key}", method = DELETE, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity removePreference(@PathVariable("key") final String key) {
        this.preferencesService.removePreference(this.authService.getCurrentUser(), key);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/preferences/contains", method = GET, produces = TEXT_PLAIN_VALUE)
    public ResponseEntity containsPreferences(@RequestParam(name = "key", required = false) final String keyPreference) {
        return ResponseEntity.ok("" + this.preferencesService.containsPreference(this.authService.getCurrentUser(), keyPreference));
    }

    @RequestMapping(value = "/preferences", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity getCurrentPreferences() {
        return ResponseEntity.ok(this.preferencesService.getUserPreferences(this.authService.getCurrentUser()).getPreferences());
    }

    @RequestMapping(value = "/preferences/{key}", method = PUT, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity setPreferences(@PathVariable("key") final String key, @RequestParam(value = "value", required = false) final String value) {
        this.preferencesService.setPreference(this.authService.getCurrentUser(), key, value);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/preferences/{key}", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity getPreference(@PathVariable("key") final String key) {
        return ResponseEntity.ok(this.preferencesService.getPreference(this.authService.getCurrentUser(), key));
    }

    @RequestMapping(value = "/preferences/{key}/value", method = GET, produces = {APPLICATION_JSON_UTF8_VALUE, APPLICATION_JSON_VALUE})
    public ResponseEntity getPreferenceValue(@PathVariable("key") final String key) {
        return ResponseEntity.ok(this.preferencesService.getPreferenceValue(this.authService.getCurrentUser(), key));
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
