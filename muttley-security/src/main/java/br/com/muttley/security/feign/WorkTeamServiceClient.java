package br.com.muttley.security.feign;

import br.com.muttley.feign.service.config.FeignTimeoutConfig;
import br.com.muttley.model.security.Role;
import br.com.muttley.model.security.WorkTeam;
import br.com.muttley.security.infra.security.server.FeignClientConfig;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * @author Joel Rodrigues Moreira on 18/04/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@FeignClient(value = "${muttley.security.name-server}", path = "/api/v1/work-teams", configuration = {FeignClientConfig.class, FeignTimeoutConfig.class})
public interface WorkTeamServiceClient extends RestControllerClient<WorkTeam> {

    @RequestMapping(value = "/find-by-name", method = GET, consumes = {APPLICATION_JSON_UTF8_VALUE})
    public WorkTeam findByName(@RequestParam(name = "name", defaultValue = "") final String name);

    @RequestMapping(value = "/roles/current-roles", method = GET, consumes = {APPLICATION_JSON_UTF8_VALUE})
    public Set<Role> loadCurrentRoles();

    @RequestMapping(value = "/avaliable-roles", method = GET, consumes = {APPLICATION_JSON_UTF8_VALUE})
    public Set<Role> loadAvaliableRoles();

    @RequestMapping(value = "/find-by-user", method = GET, consumes = {APPLICATION_JSON_UTF8_VALUE})
    List<WorkTeam> findByUser();
}
