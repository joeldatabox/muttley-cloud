package br.com.muttley.security.infra.feign;

import br.com.muttley.feign.autoconfig.FeignTimeoutConfig;
import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.infra.server.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security-server.name-server}", path = "/api/v1/user-preferences", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class})
public interface UserPreferenceServiceClient {

    @RequestMapping(method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    public UserPreferences getPreferences();

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    public void setPreference(@RequestBody final Preference preference);

    @RequestMapping(value = "/email/{email}", method = POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    public void setPreferenceByEmail(@PathVariable("email") final String email, @RequestBody final Preference preference);

    @RequestMapping(value = "/{key}", method = DELETE, consumes = APPLICATION_JSON_UTF8_VALUE)
    public void removePreference(@PathVariable("key") final String key);
}
