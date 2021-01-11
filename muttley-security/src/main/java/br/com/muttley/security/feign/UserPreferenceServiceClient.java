package br.com.muttley.security.feign;

import br.com.muttley.feign.service.config.FeignTimeoutConfig;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security.name-server}", path = "/api/v1/user-preferences", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class})
public interface UserPreferenceServiceClient {

    @RequestMapping(method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    UserPreferences getUserPreferences();

    @RequestMapping(value = "/preferences", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    void setPreference(@RequestBody final Preference preference);

    @RequestMapping(value = "/preferences/{key}", method = DELETE, consumes = APPLICATION_JSON_UTF8_VALUE)
    void removePreference(@PathVariable("key") final String key);

    @RequestMapping(value = "/preferences/contains", method = GET, produces = TEXT_PLAIN_VALUE)
    boolean containsPreferences(@RequestParam(name = "key", required = false) final String keyPreference);

    @RequestMapping(value = "/preferences", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    Set<Preference> getCurrentPreferences();

    @RequestMapping(value = "/preferences/{key}", method = PUT, consumes = APPLICATION_JSON_UTF8_VALUE)
    void setPreferences(@PathVariable("key") final String key, @RequestParam(value = "value", required = false) final String value);

    @RequestMapping(value = "/preferences/{key}", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    Preference getPreference(@PathVariable("key") final String key);

    @RequestMapping(value = "/preferences/{key}/value", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    String getPreferenceValue(@PathVariable("key") final String key);

    @RequestMapping(value = "/users-from-preference", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    List<User> getUsersFromPreference(@RequestParam(name = "key", required = false) final String key, @RequestParam(name = "value", required = false) final String value);

    @RequestMapping(value = "/user-from-preference", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    User getUserFromPreference(@RequestParam(name = "key", required = false) final String key, @RequestParam(name = "value", required = false) final String value);
}
