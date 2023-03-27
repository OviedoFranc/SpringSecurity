package com.login.TokenLogin.Security.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserCacheService{
    private final Map<String, UserDetails> cache = new HashMap<>();

    public UserDetails getUserFromCache(String token) {
        return cache.get(token);
    }

    public void putUserInCache(String token,UserDetails user) {
        cache.put(token , user);
    }
    //TODO UTILIZAR AL MOMENTO DE HACER UN LOGOUT PARA BORRAR DEL CACHE AL USER
    public void removeUserFromCache(String token) {
        cache.remove(token);
    }
}