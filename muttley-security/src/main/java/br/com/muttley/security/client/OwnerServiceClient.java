package br.com.muttley.security.client;

import br.com.muttley.model.security.User;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security.name-server}", url = "/api/v1/owner", configuration = FeignClientConfig.class)
public interface OwnerServiceClient extends RestControllerClient<User> {

}
