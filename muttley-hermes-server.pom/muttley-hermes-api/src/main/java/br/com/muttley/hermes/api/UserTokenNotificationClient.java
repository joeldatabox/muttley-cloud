package br.com.muttley.hermes.api;


import br.com.muttley.model.hermes.notification.TokenId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import br.com.muttley.security.infra.security.server.FeignClientConfig;

@FeignClient(value = "${muttley.hermes.server.name}", path = "/api/v1/tokens-notification", configuration = FeignClientConfig.class)
public interface UserTokenNotificationClient {

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    public void save(@RequestBody TokenId tokenId);

}
