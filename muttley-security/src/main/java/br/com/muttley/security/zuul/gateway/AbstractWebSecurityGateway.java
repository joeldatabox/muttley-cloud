package br.com.muttley.security.zuul.gateway;

import br.com.muttley.security.infra.component.AuthenticationTokenFilterGateway;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Abstrai todas as configurações necessárias para utlizar o springsecurity
 *
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
/*@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)*/
public abstract class AbstractWebSecurityGateway extends WebSecurityConfigurerAdapter {
    protected final String loginEndPoint;
    protected final String refreshTokenEndPoin;
    protected final String createEndPoint;
    protected final UnauthorizedHandler unauthorizedHandler;
    protected final AuthenticationTokenFilterGateway authenticationTokenFilterGateway;

    @Autowired
    public AbstractWebSecurityGateway(
            @Value("${muttley.security.jwt.controller.loginEndPoint}") final String loginEndPoint,
            @Value("${muttley.security.jwt.controller.refreshEndPoint}") final String refreshTokenEndPoin,
            @Value("${muttley.security.jwt.controller.createEndPoint}") final String createEndPoint,
            final UnauthorizedHandler unauthorizedHandler,
            final AuthenticationTokenFilterGateway authenticationTokenFilterGateway) {
        this.loginEndPoint = loginEndPoint;
        this.refreshTokenEndPoin = refreshTokenEndPoin;
        this.createEndPoint = createEndPoint;
        this.unauthorizedHandler = unauthorizedHandler;

        this.authenticationTokenFilterGateway = authenticationTokenFilterGateway;
    }


    /*@Autowired
    protected void configureAuthentication(AuthenticationManagerBuilder authentication) throws Exception {
        authentication
                .userDetailsService(new UserDetailsService() {
                    @Override
                    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
                        return null;
                    }
                })
                .passwordEncoder(passwordEncoder());
    }*/

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //não é necessário CSRF pois nossos tokens evita isso
                .csrf().disable()
                //ouvinte que despacha requisiçõe não autorizadas
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .exceptionHandling().accessDeniedPage("/403").and()
                //desativando o controle de sessão
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                //.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // permite acesso a qualquer recurso estatico
                .antMatchers(
                        HttpMethod.GET,
                        this.endPointPermitAllToGet()
                ).permitAll()
                //permitindo acesso aos endpoint de login
                .antMatchers(loginEndPoint, refreshTokenEndPoin, createEndPoint).permitAll()
                //barrando qualquer outra requisição não autenticada
                .anyRequest().authenticated();

        //adicionando o filtro de segurança
        http.addFilterBefore(authenticationTokenFilterGateway, UsernamePasswordAuthenticationFilter.class);

        //desabilitando controle de cache
        http.headers().cacheControl();
    }

    /**
     * Informa uma lista de endpoits que são livres de segurança.
     * Por exemplo, deve-se listar aqui os end points referente a arquivos estaticos
     *
     * @return um array de padrões de urls
     */
    protected abstract String[] endPointPermitAllToGet();
}
