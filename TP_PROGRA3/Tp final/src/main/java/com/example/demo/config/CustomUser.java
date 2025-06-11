package com.example.demo.config; // Puedes ajustar el paquete

import com.example.demo.entities.Usuario; // Asegúrate de importar tu entidad Usuario
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


public class CustomUser extends User {

    private final Long id;

    public CustomUser(
            Long id,
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

}
