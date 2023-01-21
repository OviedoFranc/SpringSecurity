package com.login.TokenLogin.Security;

import com.login.TokenLogin.Model.DTO.DTOUserReg;
import com.login.TokenLogin.Model.User;
import com.login.TokenLogin.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    // inyecto el repositorio de usuarios
    @Autowired
    private UserRepository userRepository;

    public void registerUser(DTOUserReg usuario){
        User user = new User(usuario.nombre(),
                             usuario.email(),
                             usuario.password(),
                             usuario.rol() );
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User usuario = userRepository
                .findByEmail(email)
                .orElseThrow( () -> new UsernameNotFoundException
                                        (" El usuario con email  "+ email +" no existe.") );


        return new UserDetailsImpl(usuario);
    }
}