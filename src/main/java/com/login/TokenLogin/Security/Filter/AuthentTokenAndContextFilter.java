package com.login.TokenLogin.Security.Filter;

import com.login.TokenLogin.Security.Service.JWTTokenService;

import com.login.TokenLogin.Security.Service.UserCacheService;
import com.login.TokenLogin.Security.Service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//TODO DOCUMENTAR
@Component
public class AuthentTokenAndContextFilter extends OncePerRequestFilter {
    @Autowired
    private UserService UserService;
    @Autowired
    private JWTTokenService TokenService;
    @Autowired
    private UserCacheService UserCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
                                    FilterChain filterChain)  throws IOException, ServletException {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer") ){

            String token = bearerToken.replace("Bearer ", "");

            if ( SecurityContextHolder.getContext().getAuthentication() == null && !token.isEmpty()) {

                //Extraigo del cache el user con el token
                UserDetails userDetails = UserCache.getUserFromCache(token);

                //EN CASO DE SER NULO LO CARGO DE LA DB Y LO GUARDO EN CACHE
                if (userDetails == null) {
                    String email = JWTTokenService.extractSpecificClaim(token, Claims::getSubject);
                    userDetails = UserService.loadUserByUsername(email);
                    UserCache.putUserInCache(token, userDetails);
                }
                //VERIFICO QUE EL TOKEN SEA CORRECTO Y ACTUALIZO EL SECURITY CONTEXT
                if(userDetails != null &&  TokenService.isTokenValid(token,userDetails)  ) {

                    UsernamePasswordAuthenticationToken authenticatedToken = JWTTokenService.getAuthentication(token);
                    //Representa los detalles específicos de la autenticación web, como la dirección IP del cliente
                    // y la información del navegador utilizado para la autenticación.
                    authenticatedToken.setDetails(  new WebAuthenticationDetailsSource().buildDetails(request) );
                    SecurityContextHolder.getContext().setAuthentication(authenticatedToken);
                }
                else {
                    //TODO MANEJO DE ERRORES EN CASO DE QUE EL TOKEN NO SEA VALIDO O NO EXISTA EL USER
                }
            }

        }

        filterChain.doFilter(request,response);

    }
}