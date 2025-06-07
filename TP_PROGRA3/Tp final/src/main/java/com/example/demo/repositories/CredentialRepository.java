package com.example.demo.repositories;
import com.example.demo.entities.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credencial, Long> {
    Optional<Credencial> findByUserId(Long userId);
}