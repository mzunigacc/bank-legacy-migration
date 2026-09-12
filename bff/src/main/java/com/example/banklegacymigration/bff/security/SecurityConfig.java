package com.example.banklegacymigration.bff.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/web/**").hasRole("WEB")
                        .requestMatchers("/api/mobile/**").hasRole("MOBILE")
                        .requestMatchers("/api/atm/**").hasRole("ATM")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder) {

        UserDetails webUser = User.builder()
                .username("web_user")
                .password(passwordEncoder.encode("web_pass"))
                .roles("WEB")
                .build();

        UserDetails mobileUser = User.builder()
                .username("mobile_user")
                .password(passwordEncoder.encode("mobile_pass"))
                .roles("MOBILE")
                .build();

        UserDetails atmUser = User.builder()
                .username("atm_user")
                .password(passwordEncoder.encode("atm_pass"))
                .roles("ATM")
                .build();

        return new InMemoryUserDetailsManager(
                webUser,
                mobileUser,
                atmUser
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories
                .createDelegatingPasswordEncoder();
    }
}