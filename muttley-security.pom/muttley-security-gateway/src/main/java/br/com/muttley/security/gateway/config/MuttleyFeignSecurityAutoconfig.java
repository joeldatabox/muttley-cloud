package br.com.muttley.security.gateway.config;

import br.com.muttley.security.infra.properties.MuttleySecurityProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * @author Joel Rodrigues Moreira on 25/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
@EnableConfigurationProperties(MuttleySecurityProperties.class)
@EnableFeignClients(basePackages = "br.com.muttley.security.infra.feign")
public class MuttleyFeignSecurityAutoconfig implements InitializingBean {

    private final MuttleySecurityProperties property;

    @Autowired
    public MuttleyFeignSecurityAutoconfig(MuttleySecurityProperties property) {
        this.property = property;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final Logger log = LoggerFactory.getLogger(MuttleyFeignSecurityAutoconfig.class);
        if (isEmpty(property.getSecurityServer().getNameServer())) {
            log.error("Please, set property ${muttley.security-server.name-server}");
        } else {
            log.info("Configured clients in package br.com.muttley.security.feign");
        }
    }
}
