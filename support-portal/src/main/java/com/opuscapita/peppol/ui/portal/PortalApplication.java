package com.opuscapita.peppol.ui.portal;

import com.opuscapita.peppol.ui.portal.security.UmsAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;

@SpringBootApplication(scanBasePackages = {"com.opuscapita.peppol.ui.portal", "com.opuscapita.peppol.commons"})
@EnableSpringHttpSession
@EnableJpaRepositories(basePackages = {"com.opuscapita.peppol.ui.portal.model"})
@EntityScan(basePackages = {"com.opuscapita.peppol.commons.model", "com.opuscapita.peppol.commons.revised_model"})
@EnableDiscoveryClient
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, proxyTargetClass = true)
public class PortalApplication extends WebSecurityConfigurerAdapter {
    public static void main(String[] args) {
        SpringApplication.run(PortalApplication.class, args);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().
                exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")).accessDeniedPage("/accessDenied")
                .and().authorizeRequests()
                .antMatchers("/VAADIN/**", "/PUSH/**", "/UIDL/**", "/login", "/login/**", "/error/**", "/accessDenied/**", "/vaadinServlet/**").permitAll()
                .antMatchers("/authorized", "/**").fullyAuthenticated();
    }

    @Bean
    public UmsAuthenticationProvider umsAuthenticationProvider(@Value("${ums.server.url}") String umsServerUrl) {
        System.setProperty("ums.server.url", umsServerUrl);
        return new UmsAuthenticationProvider();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, @Value("${ums.server.url}") String umsServerUrl) throws Exception {
        auth.authenticationProvider(umsAuthenticationProvider(umsServerUrl));
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
