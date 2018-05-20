package br.com.muttley.domain.service.config;

import br.com.muttley.model.jackson.DefaultDateFormatConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author Joel Rodrigues Moreira on 19/05/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 */
@Configuration
public class DateFormatConfig {

    @Bean
    @Primary
    public DefaultDateFormatConfig createDefaultDateFormatConfig(@Value("${br.com.muttley.jackson.date-pattern:yyyy-MM-dd'T'HH:mm:ss.SSSZ}") final String pattern) {
        return new DefaultDateFormatConfig(pattern);
    }
}
