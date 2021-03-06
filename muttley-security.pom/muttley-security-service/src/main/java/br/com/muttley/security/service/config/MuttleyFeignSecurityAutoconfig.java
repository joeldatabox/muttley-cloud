package br.com.muttley.security.service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 25/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
@EnableFeignClients(basePackages = "br.com.muttley.security.infra.feign")
public class MuttleyFeignSecurityAutoconfig implements InitializingBean {

    /*@Autowired
    private MuttleySecurityProperties property;*/

    @Override
    public void afterPropertiesSet() throws Exception {
        final Logger log = LoggerFactory.getLogger(MuttleyFeignSecurityAutoconfig.class);
        /*if (isEmpty(property.getSecurityServer().getNameServer())) {
            log.error("Please, set property ${muttley.security-server.name-server}");
        } else {
            log.info("Configured clients in package br.com.muttley.security.feign");
        }*/
    }
}
