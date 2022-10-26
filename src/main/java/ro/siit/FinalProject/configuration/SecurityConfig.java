package ro.siit.FinalProject.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ro.siit.FinalProject.service.CustomUserDetailsService;

@Configuration
public class SecurityConfig {
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .authorizeHttpRequests((authz) -> {
                    try {
                        authz
                                .antMatchers("/invoiceManagement/**", "/addInvoice/**", "supplierManagement/**").authenticated()
                                .anyRequest().permitAll()
                                .and()
                                .formLogin()
                                .usernameParameter("username")
                                .defaultSuccessUrl("/invoiceManagement")
                                .permitAll()
                                .and()
                                .logout().logoutSuccessUrl("/").permitAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
                .csrf().disable();
        return http.build();
    }
}
