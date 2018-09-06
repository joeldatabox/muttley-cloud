package br.com.muttley.security.service.config;

import br.com.muttley.security.infra.component.UnauthorizedHandler;
import br.com.muttley.security.service.components.AuthenticationTokenFilterClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author Joel Rodrigues Moreira on 31/08/18.
 * e-mail: <a href="mailto:joel.databox@gmail.com">joel.databox@gmail.com</a>
 * @project muttley-cloud
 * <p>
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UnauthorizedHandler unauthorizedHandler;
    private final AuthenticationTokenFilterClient authenticationTokenFilterClient;

    @Autowired
    public WebSecurityConfig(final UnauthorizedHandler unauthorizedHandler, final AuthenticationTokenFilterClient authenticationTokenFilterClient) {
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
                //.antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // permite acesso a qualquer recurso estatico
                //.antMatchers(
                //        HttpMethod.GET,
                //        "/",
                //        "/*.html",
                //        "/**/*.{png,jpg,jpeg,svg.ico}",
                //        "/**/*.{html,css,js,svg,woff,woff2}",
                //        //endpoit padrão da aplicação
                //        "/login",
                //        "/create-user",
                //        "/home/**"
                // ).permitAll()
                //permitindo acesso aos endpoint de login
                //.antMatchers(loginEndPoint, refreshTokenEndPoin, createEndPoint).permitAll()
                //barrando qualquer outra requisição não autenticada
                .anyRequest()
                .authenticated();

        //adicionando o filtro de segurança
        http.addFilterBefore(this.authenticationTokenFilterClient, UsernamePasswordAuthenticationFilter.class);

        //desabilitando controle de cache
        http.headers().cacheControl();
    }


}
