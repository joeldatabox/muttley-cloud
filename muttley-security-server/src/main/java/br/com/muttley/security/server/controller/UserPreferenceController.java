package br.com.muttley.security.server.controller;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.security.server.service.UserPreferenceService;
import br.com.muttley.security.server.service.UserService;
import br.com.muttley.security.server.service.impl.JwtTokenUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

    private final JwtTokenUtilService tokenUtil;
    private final UserPreferenceService service;
    private final UserService userService;

    @Autowired
    public UserPreferenceController(final UserPreferenceService service, final UserService userService, final JwtTokenUtilService tokenUtil) {
        this.service = service;
        this.userService = userService;
        this.tokenUtil = tokenUtil;
    }

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity getPreferences(@RequestHeader(value = "${muttley.security.jwt.controller.token-header-jwt:Authorization-jwt}", defaultValue = "") final String token) {
        return ResponseEntity.ok(service.getPreferences(new JwtToken(token)));
    }

    @RequestMapping(method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity setPreference(@RequestHeader(value = "${muttley.security.jwt.controller.token-header-jwt:Authorization-jwt}", defaultValue = "") final String token, @RequestBody final Preference preference) {
        this.service.setPreferences(new JwtToken(token), preference);
        return ResponseEntity.ok().build();

    }

    @RequestMapping(value = "/{key}", method = RequestMethod.DELETE, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity removePreference(@RequestHeader(value = "${muttley.security.jwt.controller.token-header-jwt:Authorization-jwt}", defaultValue = "") final String token, @PathVariable("key") final String key) {
        this.service.removePreference(new JwtToken(token), key);
        return ResponseEntity.ok().build();
    }
}
