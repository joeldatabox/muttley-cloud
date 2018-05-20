package br.com.muttley.security.server.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Joel Rodrigues Moreira on 14/01/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project spring-cloud
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityClientConfig extends WebSecurityConfigurerAdapter {
    private final String userName;
    private final String passWord;
    private final String role;

    public WebSecurityClientConfig(
            @Value("${muttley.security-server.user.name}") final String userName,
            @Value("${muttley.security-server.user.password}") final String passWord,
            @Value("${muttley.security-server.user.role}") final String role) {
        this.userName = userName;
        this.passWord = passWord;
        this.role = role;
    }

    /**
     * Configurando usuário e senha necessário para autenticação no serviço
     */
    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser(this.userName)
                .password(this.passWord)
                .roles(this.role);
    }

    /**
     * Configurando a segurança exigida para acessar o serviço
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .hasRole(this.role)
                .and()
                .httpBasic()
                .and()
                .csrf()
                .disable();
    }
}