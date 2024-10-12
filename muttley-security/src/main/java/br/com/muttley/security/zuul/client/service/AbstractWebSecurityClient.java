package br.com.muttley.security.zuul.client.service;

import br.com.muttley.security.infra.component.AuthenticationTokenFilterClient;
import br.com.muttley.security.infra.component.UnauthorizedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by joel on 26/03/17.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AbstractWebSecurityClient extends WebSecurityConfigurerAdapter {
    private final UnauthorizedHandler unauthorizedHandler;
    private final AuthenticationTokenFilterClient authenticationTokenFilterClient;

    @Autowired
    public AbstractWebSecurityClient(final UnauthorizedHandler unauthorizedHandler, final AuthenticationTokenFilterClient authenticationTokenFilterClient) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.authenticationTokenFilterClient = authenticationTokenFilterClient;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                .antMatchers(HttpMethod.GET, this.endPointPermitAllToGet()).permitAll()
                .antMatchers(HttpMethod.POST, this.endPointPermitAllToPost()).permitAll()
                .antMatchers(HttpMethod.PUT, this.endPointPermitAllToPut()).permitAll()
                .antMatchers(HttpMethod.DELETE, this.endPointPermitAllToDelete()).permitAll()
                //permitindo acesso aos endpoint de login
                //.antMatchers(loginEndPoint, refreshTokenEndPoin, createEndPoint).permitAll()
                //barrando qualquer outra requisição não autenticada
                .anyRequest().authenticated();

        //adicionando o filtro de segurança
        http.addFilterBefore(this.authenticationTokenFilterClient, UsernamePasswordAuthenticationFilter.class);

        //desabilitando controle de cache
        http.headers().cacheControl();
    }

    /**
     * Informa uma lista de endpoits que são livres de segurança.
     * Por exemplo, deve-se listar aqui os end points referente a arquivos estaticos
     *
     * @return um array de padrões de urls
     */
    protected String[] endPointPermitAllToGet() {
        return new String[]{};
    }

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
