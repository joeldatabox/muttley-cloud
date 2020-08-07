package br.com.muttley.hermes.api;

import br.com.muttley.feign.autoconfig.FeignTimeoutConfig;
import br.com.muttley.model.hermes.notification.TokenId;
import br.com.muttley.security.infra.server.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * @author Joel Rodrigues Moreira on 05/08/2020.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.hermes.server.name}", path = "/api/v1/tokens-notification", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class})
public interface UserTokenNotificationClient {

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_VALUE)
    public void save(@RequestBody TokenId tokenId);

}
