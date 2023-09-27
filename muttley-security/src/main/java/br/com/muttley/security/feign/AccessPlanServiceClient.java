package br.com.muttley.security.feign;

import br.com.muttley.feign.service.config.FeignTimeoutConfig;
import br.com.muttley.feign.service.interceptors.HeadersMetadataInterceptor;
import br.com.muttley.model.security.AccessPlan;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security.name-server}", path = "/api/v1/access-plan", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class, HeadersMetadataInterceptor.class})
public interface AccessPlanServiceClient extends RestControllerClient<AccessPlan> {
    @RequestMapping(value = "/find-by-owner", method = RequestMethod.GET)
    AccessPlan findByOwner(@RequestParam("owner") final String idOwner);
}
