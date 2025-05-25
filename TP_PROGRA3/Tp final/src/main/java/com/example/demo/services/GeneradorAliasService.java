package com.example.demo.services;

import com.example.demo.repositories.CuentaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class GeneradorAliasService {

    private final List<String> palabras = new CopyOnWriteArrayList<>();
    private final SecureRandom random = new SecureRandom();

    @Autowired
    CuentaRepository cuentaRepository;

    @PostConstruct
    public void iniciar() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("palabras.txt").getInputStream()))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    palabras.add(linea.trim().toLowerCase());
                }
            }
            if (palabras.isEmpty()) {
                throw new IllegalStateException("El archivo de palabras para alias está vacío o no se encontró.");
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo cargar el diccionario de palabras para alias.", e);
        }
    }

    public String generarAlias() {

        StringBuilder alias = new StringBuilder();
        boolean flag = false;
        while(!flag){
            for (int i = 0; i < 3; i++) {
                int indiceAleatorio = random.nextInt(palabras.size());
                alias.append(palabras.get(indiceAleatorio));
                if (i < 2) {
                    alias.append(".");
                }
            }
            if(cuentaRepository.findByAlias(alias.toString()).isEmpty()){
                flag = true;
            }
        }
        return alias.toString();
    }
}