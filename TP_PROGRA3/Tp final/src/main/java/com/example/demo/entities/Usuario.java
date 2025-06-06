package com.example.demo.entities;

import com.example.demo.type.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"cliente", "cuentaList"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "tbl_usuario"
)
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long usuarioId;
    @Column(
            name = "nombre_usuario",
            nullable = false,
            unique = true
    )
    private String nombreUsuario;
    private int pin;
    @OneToOne(
            mappedBy = "usuario",
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties({"usuario"})
    private Cliente cliente;
    @OneToMany(
            mappedBy = "usuario",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JsonIgnoreProperties({"movimientoList", "tarjetaList"})
    private List<Cuenta> cuentaList;

    @Enumerated(EnumType.STRING)
    private Role rol;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(rol.name()));
    }

    @Override
    public String getPassword() {
        return String.valueOf(pin); // Convertimos el PIN a String para Spring Security
    }
    @Override
    public String getUsername() {
        return nombreUsuario;
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
