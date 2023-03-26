package com.login.TokenLogin.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity @Getter @Setter @NoArgsConstructor
@Table(name = "user")
public class User implements UserDetails {
    @Id @GeneratedValue( strategy = GenerationType.AUTO)
    private Integer ID;
    @Column(name = "name")
    private String name;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "rol")
    private Rol rol;

    public User(String name, String email, String password, Rol rol) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    @Override
    public String toString() {
        return "User{ nombre='" + name + '\'' +
                ", email='" + email + '\'' +
                ", rol=" + rol +
                '}';
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList() ; //Deberia retornar el rol o roles del usuario
    }


    @Override
    public String getPassword() {
        return this.password;
    }

    //SETEO COMO USERNAME EL MAIL PARA EL LOGEO
    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
