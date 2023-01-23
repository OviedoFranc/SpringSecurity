package com.login.TokenLogin.Security.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.login.TokenLogin.Model.User;
import com.login.TokenLogin.Model.DTO.AuthCredentials;
import com.login.TokenLogin.Security.TokenUtils;
import com.login.TokenLogin.Security.UserDetailsImpl;
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

@AllArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    //IMPLEMENTACION DEL INTENTO DE AUTENTICACION DE ESTE FILTRO
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
												HttpServletResponse response) throws AuthenticationException {

        AuthCredentials authCredentials = new AuthCredentials() ;
        //Cuando nos envian las credenciales en formato Json las convertimos
        try{
             authCredentials = new ObjectMapper().readValue(
                    request.getReader(),
                    AuthCredentials.class);
        } catch(IOException e) {}

        UsernamePasswordAuthenticationToken usernamePAT = new UsernamePasswordAuthenticationToken(
                authCredentials.getEmail(),
                authCredentials.getPassword(),
                Collections.emptyList() );       //en este ultimo realmente irian los roles
        return getAuthenticationManager().authenticate(usernamePAT);
    }

    // EN CASO DE QUE SE COMPLETE COMPLETAMENTE LA AUTENTICACION
    @Override
    protected void successfulAuthentication(HttpServletRequest request,
											HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();
        String token = TokenUtils.createToken(userDetails.getNombre(),
                                              userDetails.getUsername());
        // modificamos lo que es la respuesta para adjuntar el token a la respuesta HTTP del cliente
                            //Name         //Value
        response.addHeader("Authorization","Bearer " + token);
        response.getWriter().flush(); // confirmamos los cambios

        super.successfulAuthentication(request,response,chain,authResult);
    }

}