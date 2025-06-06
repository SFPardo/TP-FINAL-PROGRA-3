package com.example.demo.entities;

import com.example.demo.entities.enums.TipoCuenta;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = {"usuario", "movimientoList", "tarjetaList"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(
        name = "tbl_cuenta"
)
public class Cuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cuentaId;
    @Column(
            name = "cbu",
            nullable = false,
            unique = true
    )
    private String cbu;
    @Column(
            name = "alias",
            nullable = false,
            unique = true
    )
    private String alias;
    private BigDecimal saldo;
    @Enumerated(EnumType.STRING)
    private TipoCuenta tipoCuenta;
    private BigDecimal limiteSobregiro;
    private LocalDate fechaCreacion;
    @PrePersist
    protected void alCrear(){
        fechaCreacion = LocalDate.now();
    }
    @ManyToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "usuario_id",
            referencedColumnName = "usuarioId"
    )
    @JsonIgnoreProperties("cuentaList")
    private Usuario usuario;
    @OneToMany(
            mappedBy = "cuenta",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @JsonIgnoreProperties("cuenta")
    private List<MovimientoCuenta> movimientoList = new ArrayList<>();
    @OneToMany(
            mappedBy = "cuenta",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @JsonIgnoreProperties("cuenta")
    private List<Tarjeta> tarjetaList = new ArrayList<>();
    @UpdateTimestamp
    private LocalDateTime fechaActualizacion;

    public void addMovimiento(MovimientoCuenta movimiento) {
        movimientoList.add(movimiento);
        movimiento.setCuenta(this);
    }

    public void addTarjeta(Tarjeta tarjeta) {
        tarjetaList.add(tarjeta);
        tarjeta.setCuenta(this);
    }

}
