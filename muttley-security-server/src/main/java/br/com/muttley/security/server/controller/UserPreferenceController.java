package br.com.muttley.security.server.controller;

import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.security.server.service.UserPreferenceService;
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

    private final UserPreferenceService service;
    private final UserService userService;

    @Autowired
    public UserPreferenceController(final UserPreferenceService service, final UserService userService) {
        this.service = service;
        this.userService = userService;
    }

    @RequestMapping(value = "/{idUser}", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getPreferences(@PathVariable("idUser") String idUser) {
        return ResponseEntity.ok(this.service.getPreferences(new User().setId(idUser)));
    }

    @RequestMapping(value = "/{idUser}/preferences", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity setPreference(@PathVariable("idUser") final String idUser, @RequestBody final Preference preference) {
        this.service.setPreferences(new User().setId(idUser), preference);
        return ResponseEntity.ok().build();

    }

    @RequestMapping(value = "/{idUser}/preferences/{key}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity removePreference(@PathVariable("idUser") final String idUser, @PathVariable("key") final String key) {
        this.service.removePreference(new User().setId(idUser), key);
        return ResponseEntity.ok().build();
    }
}
