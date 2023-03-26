package com.login.TokenLogin.Security.Config;

import com.login.TokenLogin.Security.Filter.JWTAuthentication;
import com.login.TokenLogin.Security.Filter.AuthentTokenAndContextFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@AllArgsConstructor
@Configuration
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final AuthentTokenAndContextFilter authentTokenAndContextFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager)
            throws Exception {

        JWTAuthentication jwtAuthentication = new JWTAuthentication();
        jwtAuthentication.setAuthenticationManager(authManager);

        return http
                .csrf().disable() // TODO: HABILITAR PERO PARA LOS POST PASAR UN CORS TOKEN
                .cors()
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/user", "/user/**")
                .permitAll()
                .requestMatchers("/adminPanel").hasAnyRole("ADMIN")
                // entramos a las reglas de las solicitudes
                .anyRequest()
                .authenticated()  //cualquier solicitud autenticada
                .and()
                .httpBasic()  //habilita la autentificacion basica (dps se desactiva)
                .and()
                .sessionManagement() // entramos a la gestion de las sesiones
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // sin estado
                .and()
                .addFilter( jwtAuthentication )
                .addFilterBefore(authentTokenAndContextFilter, UsernamePasswordAuthenticationFilter.class)
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
        PasswordEncoder passwordEncoder () {
            return new BCryptPasswordEncoder();
        }
}

