package br.com.muttley.security.autoconfig;

import br.com.muttley.security.properties.MuttleySecurityProperty;
import br.com.muttley.security.zuul.client.config.WebSecurityClientConfig;
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
@EnableConfigurationProperties(MuttleySecurityProperty.class)
@EnableFeignClients(basePackages = "br.com.muttley.security.feign")
public class MuttleyFeignSecurityAutoconfig implements InitializingBean {

    @Autowired
    private MuttleySecurityProperty property;

    @Override
    public void afterPropertiesSet() throws Exception {
        final Logger log = LoggerFactory.getLogger(WebSecurityClientConfig.class);
        if (isEmpty(property.getSecurityServer().getNameServer())) {
            log.error("Please, set property ${muttley.security-server.name-server}");
        } else {
            log.info("Configured clients in package br.com.muttley.security.feign");
        }
    }
}
