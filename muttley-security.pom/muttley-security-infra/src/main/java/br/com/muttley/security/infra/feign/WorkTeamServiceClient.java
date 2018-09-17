package br.com.muttley.security.infra.feign;

import br.com.muttley.feign.autoconfig.FeignTimeoutConfig;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.security.infra.server.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security-server.name-server}", path = "/api/v1/work-teams", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class})
public interface WorkTeamServiceClient extends RestControllerClient<WorkTeam> {

}
