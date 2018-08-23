package br.com.muttley.security.zuul.gateway;

import br.com.muttley.model.security.JwtUser;
import br.com.muttley.security.feign.UserServiceClient;
import br.com.muttley.security.infra.component.AuthenticationTokenFilterGateway;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.infra.service.EndpointsPermitAll;
import br.com.muttley.security.properties.MuttleySecurityProperty;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Abstrai todas as configurações necessárias para utlizar o springsecurity
 *
 * @author Joel Rodrigues Moreira on 12/01/18.
 * @project spring-cloud
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityGateway extends WebSecurityConfigurerAdapter {
    @Autowired
    private MuttleySecurityProperty property;
    @Autowired
    private ObjectProvider<EndpointsPermitAll> customizeEndpoints;
    protected final UnauthorizedHandler unauthorizedHandler;
    protected final AuthenticationTokenFilterGateway authenticationTokenFilterGateway;
    protected final UserServiceClient userServiceClient;

    @Autowired
    public WebSecurityGateway(
            final UnauthorizedHandler unauthorizedHandler,
            final AuthenticationTokenFilterGateway authenticationTokenFilterGateway,
            final UserServiceClient userServiceClient) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.authenticationTokenFilterGateway = authenticationTokenFilterGateway;
        this.userServiceClient = userServiceClient;
    }


    @Autowired
    protected void configureAuthentication(AuthenticationManagerBuilder authentication) throws Exception {
        authentication
                .userDetailsService(new UserDetailsService() {
                    @Override
                    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
                        return new JwtUser(userServiceClient.findByEmail(username));
                    }
                })
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //não é necessário CSRF pois nossos tokens evita isso
                .csrf().disable()
                //ouvinte que despacha requisiçõe não autorizadas
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .exceptionHandling().accessDeniedPage("/403").and()
                //desativando o controle de sessão
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        final EndpointsPermitAll endpointsPermitAll = this.customizeEndpoints.getIfAvailable();
        if (endpointsPermitAll != null) {
            // permite acesso a qualquer recurso estatico
            http.authorizeRequests().antMatchers(
                    HttpMethod.GET,
                    endpointsPermitAll.forGetMethod()
            ).permitAll();
        }
        //permitindo acesso aos endpoint de login
        http.authorizeRequests().antMatchers(
                property.getSecurity().getJwt().getController().getLoginEndPoint(),
                property.getSecurity().getJwt().getController().getRefreshEndPoint(),
                property.getSecurity().getJwt().getController().getCreateEndPoint()
        ).permitAll()
                //barrando qualquer outra requisição não autenticada
                .anyRequest().authenticated();

        //adicionando o filtro de segurança
        http.addFilterBefore(authenticationTokenFilterGateway, UsernamePasswordAuthenticationFilter.class);

        //desabilitando controle de cache
        http.headers().cacheControl();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
