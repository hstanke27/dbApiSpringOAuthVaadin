package com.example.application.security;

import com.vaadin.flow.spring.security.VaadinWebSecurityConfigurerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SecurityConfiguration extends VaadinWebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
                .antMatchers("/oauth2/authorization/**", "/login/oauth2/callback/**").permitAll();

        httpSecurity.oauth2Login(oauth -> {
                    oauth.defaultSuccessUrl("https://samplegae.com/start");
                })
                .logout(logout -> {
                    logout.logoutSuccessUrl("/")
                        .permitAll()
                        .deleteCookies("JSESSIONID");
                });

        super.configure(httpSecurity);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().antMatchers(
                "/images/**"
        );
    }

    @Bean
    public RestOperations restOperations() {
        return new RestTemplate();
    }

    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * Attention - Must be overridden but can be empty. Otherwise, in Google Cloud App Engine, Stacktrace error will happen.
     * See http://blog.iampfac.com/blog/2015/02/12/stackoverflow-error-with-spring-security-authentication/
     * On localhost this error doesn't happen.
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    }

}