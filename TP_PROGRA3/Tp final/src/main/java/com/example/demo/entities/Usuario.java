package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
//@ToString(exclude = {"cliente", "cuentaList"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "tbl_usuario"
)
public class Usuario {
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
}
