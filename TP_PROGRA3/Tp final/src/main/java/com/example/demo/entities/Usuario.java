package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "tbl_usuario"
)
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long usuarioId;
    @Column(
            name = "nombre_usuario",
            nullable = false,
            unique = true
    )
    private String nombreUsuario;
    private int pin;
    @OneToOne(
            mappedBy = "usuario",
            fetch = FetchType.EAGER
    )
    private Cliente cliente;
    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "usuario_id",
            referencedColumnName = "usuarioId"
    )
    private List<Cuenta> cuentaList;
}
