package com.login.TokenLogin.Security.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTTokenService {

    private final static String ACCESS_TOKEN_SECRET = "2b234d0a3544d5491d856c89f771f717";
    private final static Long ACCESS_TOKE_VALIDITY_SECONDS = 2_592_000L;


    public static String createToken(UserDetails userDetails,Map<String, Object> extraClaims){

        Date dateNow = new Date( System.currentTimeMillis() );

        long expirationTime = ACCESS_TOKE_VALIDITY_SECONDS * 1_000;
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

        return Jwts.builder()
                .setSubject( userDetails.getUsername() )
                .setIssuedAt(dateNow)
                .setExpiration(expirationDate)
                .addClaims(extraClaims)
                .signWith(Keys.hmacShaKeyFor(ACCESS_TOKEN_SECRET.getBytes())
                            ) //TODO ,SignatureAlgorithm.HS256
                .compact();
    }

    // Validation token
    public boolean isTokenValid(String token, UserDetails userDetails){
        String username = extractSpecificClaim( token
                                               ,Claims:: getSubject);
        return username.equals( userDetails.getUsername() ) && !isTokenExpired(token);
    }
    private Date extractExpiration(String token){
        return extractSpecificClaim(token, Claims::getExpiration);
    }
    private boolean isTokenExpired (String token){
        return extractExpiration(token).before( new Date());
    }

    // Extract claims
    public static <T> T extractSpecificClaim (String token, Function <Claims, T> claimsResolver){
        Claims claims = extractClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    private static Claims extractClaimsFromToken(String token){
            return Jwts.parserBuilder()
                    .setSigningKey(ACCESS_TOKEN_SECRET.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }

    public static UsernamePasswordAuthenticationToken getAuthentication(String token){
        try{

            String email = extractSpecificClaim( token, Claims:: getSubject );

            return new UsernamePasswordAuthenticationToken(email,null , Collections.emptyList() );
        }
        catch(JwtException e){
            throw new JwtException("Error al obtener credenciales de autenticación", e);
        }

    }


}