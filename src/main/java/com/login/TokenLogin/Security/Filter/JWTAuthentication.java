package com.login.TokenLogin.Security.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.login.TokenLogin.Model.User;
import com.login.TokenLogin.Security.Model.AuthCredentials;
import com.login.TokenLogin.Security.Service.JWTTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@AllArgsConstructor
public class JWTAuthentication extends UsernamePasswordAuthenticationFilter {


    //INTENTO DE AUTENTICACION DE SOLICITUD HTTP
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
												HttpServletResponse response) throws AuthenticationException {

        AuthCredentials authCredentials = new AuthCredentials() ;
        //Cuando nos envian las credenciales en formato Json las convertimos y leemos
        try{
             authCredentials = new ObjectMapper().readValue(
                    request.getReader(),
                    AuthCredentials.class);
        } catch(IOException e) {

            logger.error(  request );
            logger.error("Error al convertir las credenciales de autenticación a objeto AuthCredentials", e);
        }

        UsernamePasswordAuthenticationToken usernamePAT = new UsernamePasswordAuthenticationToken(
                authCredentials.getEmail(),
                authCredentials.getPassword(),
                Collections.emptyList() );       //TODO en este ultimo realmente irian los roles
        return getAuthenticationManager().authenticate(usernamePAT);
    }

    // EN CASO DE QUE SE COMPLETE SATISFACTORIAMENTE LA AUTENTICACION SE EJECUTA ESTA RESPUESTA
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
											HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        //Al hacer una autenticacion exitosa creo un token y lo devuelvo
        User userDetails = (User) authResult.getPrincipal();

        //Creo un token y le asigno el user, y extraClaims que quiero pasar en el token
        String token = JWTTokenService.createToken( userDetails,
                                                Map.of("Name", userDetails.getName(),
                                                        "Rol", userDetails.getRol()) );

        // Adjunto a la respuesta HTTP el token
        response.addHeader("Authorization","Bearer " + token);
        response.getWriter().flush(); // confirmamos los cambios

        super.successfulAuthentication(request,response,chain,authResult);
    }

}