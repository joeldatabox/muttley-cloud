package br.com.muttley.security.zuul.gateway;

import br.com.muttley.model.security.JwtUser;
import br.com.muttley.model.security.Password;
import br.com.muttley.model.security.User;
import br.com.muttley.security.feign.UserServiceClient;
import br.com.muttley.security.infra.component.AuthenticationTokenFilterGateway;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    protected final String forgotPasswordEndPoint;
    protected final String createEndPoint;
    protected final String resetPassword;
    protected final UnauthorizedHandler unauthorizedHandler;
    protected final AuthenticationTokenFilterGateway authenticationTokenFilterGateway;
    protected final UserServiceClient userServiceClient;

    @Autowired
    public AbstractWebSecurityGateway(
            @Value("${muttley.security.jwt.controller.loginEndPoint}") final String loginEndPoint,
            @Value("${muttley.security.jwt.controller.refreshEndPoint}") final String refreshTokenEndPoin,
            @Value("${muttley.security.jwt.controller.forgotPasswordEndPoint}") final String forgotPassword,
            @Value("${muttley.security.jwt.controller.createEndPoint}") final String createEndPoint,
            @Value("${muttley.security.jwt.controller.resetPassword}") final String resetPassword,
            final UnauthorizedHandler unauthorizedHandler,
            final AuthenticationTokenFilterGateway authenticationTokenFilterGateway,
            final UserServiceClient userServiceClient) {
        this.loginEndPoint = loginEndPoint;
        this.refreshTokenEndPoin = refreshTokenEndPoin;
        this.forgotPasswordEndPoint = forgotPassword;
        this.createEndPoint = createEndPoint;
        this.resetPassword = resetPassword;
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
                        final User user = userServiceClient.findByUserName(username);
                        final Password password = userServiceClient.loadPasswordById(user.getId());
                        return JwtUser.Builder.newInstance()
                                .set(user)
                                .setPassword(password)
                                .build();
                    }
                })
                .passwordEncoder(Password.BuilderPasswordEncoder.getPasswordEncoder());
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
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                //.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // permite acesso a qualquer recurso estatico
                .antMatchers(
                        HttpMethod.GET,
                        this.endPointPermitAllToGet()
                ).permitAll()
                .antMatchers(HttpMethod.POST, this.endPointPermitAllToPost()).permitAll()
                .antMatchers(HttpMethod.PUT, this.endPointPermitAllToPut()).permitAll()
                .antMatchers(HttpMethod.DELETE, this.endPointPermitAllToDelete()).permitAll()
                //permitindo acesso aos endpoint de login
                .antMatchers(loginEndPoint, refreshTokenEndPoin, forgotPasswordEndPoint, createEndPoint, resetPassword).permitAll()
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

    protected String[] endPointPermitAllToPost() {
        return new String[]{};
    }

    protected String[] endPointPermitAllToPut() {
        return new String[]{};
    }

    protected String[] endPointPermitAllToDelete() {
        return new String[]{};
    }
}
