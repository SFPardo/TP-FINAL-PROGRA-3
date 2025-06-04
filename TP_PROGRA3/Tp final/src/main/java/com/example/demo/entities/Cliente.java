package com.example.demo.entities;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
//@ToString(exclude = {"usuario"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "tbl_cliente"
)

public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long clienteId;
    private String nombre;
    @Column(
            name = "dni",
            nullable = false,
            unique = true
    )
    private String dni;
    @Column(
            name = "email",
            nullable = false,
            unique = true
    )
    private String email;
    @Column(
            name = "telefono",
            nullable = false,
            unique = true
    )
    private String telefono;
    @Embedded
    private Domicilio domicilio;

    @OneToOne(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "usuario_id", unique = true, nullable = false
    )
    @JsonIgnoreProperties({"cliente"})
    private Usuario usuario;
}
