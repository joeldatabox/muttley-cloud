package br.com.muttley.hermes.api;


import br.com.muttley.feign.service.config.FeignTimeoutConfig;
import br.com.muttley.model.hermes.notification.TokenId;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@FeignClient(value = "${muttley.hermes.server.name}", path = "/api/v1/tokens-notification", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class})
public interface UserTokenNotificationClient {

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    public void save(@RequestBody TokenId tokenId);

}
