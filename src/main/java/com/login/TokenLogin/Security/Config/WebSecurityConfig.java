package com.login.TokenLogin.Security.Config;

import com.login.TokenLogin.Security.Filter.JWTAuthenticationFilter;
import com.login.TokenLogin.Security.Filter.JWTAuthorizationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JWTAuthorizationFilter jwtAuthorizationFilter;

    @Bean
        SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager)
                throws Exception {

        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter();
        jwtAuthenticationFilter.setAuthenticationManager(authManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");

            return http
                    .cors()
                    .and()
                    .authorizeHttpRequests()
                    .requestMatchers("/user")
                    .permitAll()// entramos a las reglas de las solicitudes
                    .anyRequest()
                    .authenticated()  //cualquier solicitud autenticada
                    .and()
                    .httpBasic()  //habilita la autentificacion basica (dps se desactiva)
                    .and()
                    .sessionManagement() // entramos a la gestion de las sesiones
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // sin estado
                    .and()
					.addFilter(jwtAuthenticationFilter)
					.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();
        }

        @Bean
        AuthenticationManager authManager(HttpSecurity http) throws Exception {
            return http.getSharedObject(AuthenticationManagerBuilder.class) //
                    .userDetailsService(userDetailsService)
                    .passwordEncoder(passwordEncoder()) //usa el password encoder de abajo
                    .and()
                    .build();
        }


        @Bean
        PasswordEncoder passwordEncoder(){
            return new BCryptPasswordEncoder();
        }
}

