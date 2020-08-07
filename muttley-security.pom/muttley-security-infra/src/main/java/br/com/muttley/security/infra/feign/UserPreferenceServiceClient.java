package br.com.muttley.security.infra.feign;

import br.com.muttley.feign.autoconfig.FeignTimeoutConfig;
import br.com.muttley.model.security.User;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.infra.server.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security.name-server}", path = "/api/v1/user-preferences", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class})
public interface UserPreferenceServiceClient {

    @RequestMapping(method = GET, consumes = APPLICATION_JSON_VALUE)
    public UserPreferences getPreferences();

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_VALUE)
    public void setPreference(@RequestBody final Preference preference);

    @RequestMapping(value = "/userName/{userName}", method = POST, consumes = APPLICATION_JSON_VALUE)
    public void setPreferenceByUserName(@PathVariable("userName") final String userName, @RequestBody final Preference preference);

    @RequestMapping(value = "/{key}", method = DELETE, consumes = APPLICATION_JSON_VALUE)
    public void removePreference(@PathVariable("key") final String key);

    @RequestMapping(value = "/{idUser}/preferences/contains", method = GET, consumes = TEXT_PLAIN_VALUE)
    public boolean containsPreferences(@PathVariable("idUser") String idUser, @RequestParam(name = "key", required = false) final String keyPreference);

    @RequestMapping(value = "/users-from-preference", method = GET, consumes = APPLICATION_JSON_VALUE)
    public List<User> getUsersFromPreference(@RequestParam(name = "key", required = false) final String key, @RequestParam(name = "value", required = false) final String value);

    @RequestMapping(value = "/user-from-preference", method = GET, consumes = APPLICATION_JSON_VALUE)
    public User getUserFromPreference(@RequestParam(name = "key", required = false) final String key, @RequestParam(name = "value", required = false) final String value);
}
