package br.com.muttley.security.feign;

import br.com.muttley.feign.service.config.FeignTimeoutConfig;
import br.com.muttley.feign.service.interceptors.HeadersMetadataInterceptor;
import br.com.muttley.model.security.Owner;
import br.com.muttley.model.security.OwnerData;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security.name-server}", path = "/api/v1/owners", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class, HeadersMetadataInterceptor.class})
public interface OwnerServiceClient extends RestControllerClient<Owner> {
    @RequestMapping(value = "/by-user", method = GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    List<OwnerData> findByUser();

    @RequestMapping(value = "/by-user-and-id/{id}", method = RequestMethod.GET, consumes = APPLICATION_JSON_UTF8_VALUE)
    Owner findByUserAndId(@PathVariable("id") final String id);
}
