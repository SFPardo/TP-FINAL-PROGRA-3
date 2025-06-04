package com.example.demo.repositories;

import com.example.demo.entities.Cliente;
import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByDni(String dni);
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByTelefono(String telefono);
    Optional<Cliente> findByDomicilioProvincia(String provincia);
    Optional<Cliente> findByDomicilioCiudad(String ciudad);
}
