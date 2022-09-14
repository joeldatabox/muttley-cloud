package br.com.muttley.security.feign;

import br.com.muttley.feign.service.config.FeignTimeoutConfig;
import br.com.muttley.feign.service.interceptors.HeadersMetadataInterceptor;
import br.com.muttley.model.security.APIToken;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient(value = "${muttley.security.name-server}", path = "/api/v1/api-tokens", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class, HeadersMetadataInterceptor.class})
public interface APITokenClient {
    @RequestMapping(value = "/token/{token}", method = GET)
    APIToken getByToken(@PathVariable("token") final String token);
}
