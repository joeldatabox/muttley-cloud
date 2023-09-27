package br.com.muttley.security.feign;

import br.com.muttley.feign.service.config.FeignTimeoutConfig;
import br.com.muttley.feign.service.interceptors.HeadersMetadataInterceptor;
import br.com.muttley.model.security.XAPIToken;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@FeignClient(value = "${muttley.security.name-server}", path = "/api/v1/x-api-tokens", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class, HeadersMetadataInterceptor.class})
public interface XAPITokenClient {
    @RequestMapping(value = "/token", method = GET)
    XAPIToken getByToken(@RequestParam("token") final String token);
}
