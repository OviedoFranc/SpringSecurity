package com.login.TokenLogin.Security.Filter;

import com.login.TokenLogin.Security.TokenUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class AuthentTokenAndContextFilter extends OncePerRequestFilter {
    @Override   //TODO FALTA MODIFICAR PARA AGREGAR CASOS DONDE EL CONTEXTO POSEA UN TOKEN
    protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
                                    FilterChain filterChain)  throws IOException, ServletException {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer") ){
            String token = bearerToken.replace("Bearer ", ""); // extraemos el token con la utilidad creada, nos devuelve algo que SPRING SECURITY ENTIENDE

            UsernamePasswordAuthenticationToken usernamePAT = TokenUtils.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(usernamePAT);
        }

        filterChain.doFilter(request,response);

    }
}