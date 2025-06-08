package com.example.demo.entities;

import com.example.demo.entities.enums.TipoRol;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
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
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Credencial credencial;
    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    private TipoRol rol;
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;
}
