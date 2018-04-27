package br.com.muttley.security.feign;

import br.com.muttley.model.security.preference.Preference;
import br.com.muttley.model.security.preference.UserPreferences;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security.name-server}", path = "/api/v1/user-preferences", configuration = FeignClientConfig.class)
public interface UserPreferenceServiceClient {

    @RequestMapping(value = "/{idUser}", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    public UserPreferences getPreferences(@PathVariable("idUser") String idUser);

    @RequestMapping(value = "/{idUser}/preferences", method = POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    public void setPreference(@PathVariable("idUser") final String idUser, @RequestBody final Preference preference);

    @RequestMapping(value = "/{idUser}/preferences/{key}", method = DELETE, consumes = APPLICATION_JSON_UTF8_VALUE)
    public void removePreference(@PathVariable("idUser") final String idUser, @PathVariable("key") final String key);
}
