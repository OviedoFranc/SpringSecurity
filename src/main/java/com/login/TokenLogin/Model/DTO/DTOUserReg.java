package com.login.TokenLogin.Model.DTO;

import com.login.TokenLogin.Model.Rol;

public record DTOUserReg(String name,
                         String email,
                         String password,
                         Rol rol) {
}
