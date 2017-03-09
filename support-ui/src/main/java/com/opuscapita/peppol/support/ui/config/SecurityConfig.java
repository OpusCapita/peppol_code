package com.opuscapita.peppol.support.ui.config;

import com.opuscapita.peppol.support.ui.authentication.UmsAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;

/**
 * Created with IntelliJ IDEA.
 * User: KACENAR1
 * Date: 14.5.2
 * Time: 10:07
 * To change this template use File | Settings | File Templates.
 */
@Configuration
@EnableWebMvcSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/css/**", "/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/favicon.ico", "/login*", "/css/**", "/images/**").anonymous()
                .anyRequest()
                .authenticated();
        http
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/error-login")
                .loginProcessingUrl("/login")
                .permitAll();
       http
                .logout()
               // .addLogoutHandler(customLogoutHandler())
                //.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                            .invalidateHttpSession(true)
                               .permitAll();
        http.httpBasic();
        //http.addFilterAfter(digestAuthenticationFilter(), BasicAuthenticationFilter.class);
        //http.addFilter(digestAuthenticationFilter());
        http.csrf().disable();
        http.sessionManagement()
                .maximumSessions(1)
                .expiredUrl("/login")
                .and()
                .invalidSessionUrl("/login");
    }

    @Bean
    public UmsAuthenticationProvider umsAuthenticationProvider() {
        return new UmsAuthenticationProvider();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(umsAuthenticationProvider());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

