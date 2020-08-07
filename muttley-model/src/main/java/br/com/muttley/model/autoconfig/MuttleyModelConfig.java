package br.com.muttley.model.autoconfig;

import br.com.muttley.model.property.MuttleyModelProperty;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Joel Rodrigues Moreira on 17/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
//@ComponentScan(basePackageClasses = SpelResolveEventListener.class)
@EnableConfigurationProperties(MuttleyModelProperty.class)
public class MuttleyModelConfig implements InitializingBean {
    @Autowired
    MuttleyModelProperty property;

    @Bean("documentNameConfig")
    public DocumentNameConfig documentNameConfigFactory() {
        return new DocumentNameConfig(this.property);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final String LN = System.lineSeparator();
        LoggerFactory.getLogger(MuttleyModelConfig.class).info(
                new StringBuilder("Configured bean \"documentNameConfig\" whith informations ")
                        .append(LN)
                        .append("{").append(LN)
                        .append("   \"owner-document\":\"").append(property.getOwnerDocument()).append("\",").append(LN)
                        .append("   \"user-document\":\"").append(property.getUserDocument()).append("\",").append(LN)
                        .append("   \"access-plan-document\":\"").append(property.getAccessPlanDocument()).append("\",").append(LN)
                        .append("   \"user-preference-document\":\"").append(property.getUserPreferenceDocument()).append("\",").append(LN)
                        .append("   \"work-team-document\":\"").append(property.getWorkTeamDocument()).append("\"").append(LN)
                        .append("}")
                        .append(LN)
                        .toString()
        );
    }
}
