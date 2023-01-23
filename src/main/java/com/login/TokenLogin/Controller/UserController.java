package com.login.TokenLogin.Controller;

import com.login.TokenLogin.Model.User;
import com.login.TokenLogin.Repository.UserRepository;
import com.login.TokenLogin.Security.UserDetailsServiceImpl;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.login.TokenLogin.Model.DTO.DTOUserReg;

import java.util.List;

@RestController
public class UserController {

        @Autowired
        UserRepository userRepository;
        @Autowired
        UserDetailsServiceImpl UserDetailsService;

        @GetMapping("/user")
        public List<User> user(){
           return userRepository.findAll();
        }
         @GetMapping("/userWithToken")
         public List<User> userWithToken(){
            return userRepository.findAll();
         }
        @GetMapping("/adminPanel")
        public String adminPanel(){
            return "Panel de admin abierto";
        }

        @PostMapping("/user/register")
        public ResponseEntity registrarUsuario(@RequestBody DTOUserReg usuario){
            UserDetailsService.registerUser(usuario);
            return  ResponseEntity.ok(HttpStatus.OK);
        }

}
