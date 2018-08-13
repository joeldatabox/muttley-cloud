package br.com.muttley.configserver.autoconfig;

import br.com.muttley.configserver.property.MuttleyConfigServerProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração padrão de segurança para o serviço de config
 *
 * @author Joel Rodrigues Moreira on 28/12/17.
 * @project muttley-configserver
 */
@Configuration
@EnableConfigurationProperties(MuttleyConfigServerProperty.class)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final MuttleyConfigServerProperty property;

    @Autowired
    public SecurityConfig(final MuttleyConfigServerProperty property) {
        this.property = property;
    }

    /**
     * Configurando usuário e senha necessário para autenticação no serviço
     */
    @Override
    public void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .passwordEncoder(createPasswordEncoder())
                .withUser(this.property.getSecurity().getUser().getName())
                .password(
                        createPasswordEncoder().encode(this.property.getSecurity().getUser().getPassword())
                ).roles(this.property.getSecurity().getUser().getRole());
    }

    @Bean
    public PasswordEncoder createPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configurando a segurança exigida para acessar o serviço
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .hasRole(this.property.getSecurity().getUser().getRole())
                .and()
                .httpBasic()
                .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable();
    }
}
