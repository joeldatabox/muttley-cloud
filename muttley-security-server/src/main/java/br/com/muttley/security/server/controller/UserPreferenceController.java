package br.com.muttley.security.server.controller;

import br.com.muttley.model.security.JwtToken;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.security.server.service.JwtTokenUtilService;
import br.com.muttley.security.server.service.UserPreferenceService;
import br.com.muttley.security.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping(value = "/api/v1/user-preferences", produces = APPLICATION_JSON_VALUE)
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

    @RequestMapping(method = GET, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity getPreferences(@RequestHeader(value = "${muttley.security.jwt.controller.token-header-jwt:Authorization-jwt}", defaultValue = "") final String token) {
        return ResponseEntity.ok(service.getPreferences(new JwtToken(token)));
    }

    @RequestMapping(method = POST, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity setPreference(@RequestHeader(value = "${muttley.security.jwt.controller.token-header-jwt:Authorization-jwt}", defaultValue = "") final String token, @RequestBody final Preference preference) {
        this.service.setPreferences(new JwtToken(token), preference);
        return ResponseEntity.ok().build();

    }

    @RequestMapping(value = "/userName/{userName}", method = POST, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity setPreferenceByUserName(@PathVariable("userName") final String userName, @RequestBody final Preference preference) {
        this.service.setPreferences(userName, preference);
        return ResponseEntity.ok().build();

    }

    @RequestMapping(value = "/{key}", method = DELETE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity removePreference(@RequestHeader(value = "${muttley.security.jwt.controller.token-header-jwt:Authorization-jwt}", defaultValue = "") final String token, @PathVariable("key") final String key) {
        this.service.removePreference(new JwtToken(token), key);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/{idUser}/preferences/contains", method = GET, produces = TEXT_PLAIN_VALUE)
    public ResponseEntity containsPreferences(@PathVariable("idUser") String idUser, @RequestParam(name = "key", required = false) final String keyPreference) {
        return ResponseEntity.ok("" + this.userService.constainsPreference(new User().setId(idUser), keyPreference));
    }
}
