package br.com.muttley.security.server.autoconfig.security;

import br.com.muttley.security.server.property.MuttleySecurityProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

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

    @Autowired
    private MuttleySecurityProperty properties;
    @Autowired
    private UnauthorizedHandler unauthorizedHandler;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Configurando usuário e senha necessário para autenticação no serviço
     */
    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .passwordEncoder(this.passwordEncoder)
                .withUser(this.properties.getSecurityServer().getUser().getName())
                .password(this.passwordEncoder.encode(this.properties.getSecurityServer().getUser().getPassword()))
                .roles(this.properties.getSecurityServer().getUser().getRole());
    }

    /**
     * Configurando a segurança exigida para acessar o serviço
     */
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest()
                .hasRole(this.properties.getSecurityServer().getUser().getRole())
                .and()
                .httpBasic()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(STATELESS)
                .and()
                .csrf()
                .disable()
                //ouvinte que despacha requisiçõe não autorizadas
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .exceptionHandling().accessDeniedPage("/403");
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}