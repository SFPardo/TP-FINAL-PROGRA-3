package com.example.demo.services.impl;

import com.example.demo.repositories.CuentaRepository;
import com.example.demo.services.GeneradorCbuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class GeneradorCbuServiceImpl implements GeneradorCbuService {

    @Autowired
    private CuentaRepository cuentaRepository;

    private static final SecureRandom random = new SecureRandom();
    private static final int CBU_LENGTH = 22;

    @Override
    public String generarDigitos(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    @Override
    public String generarCbu() {
        String cbu = null;
        boolean flag = false;

        while (!flag) {
            cbu = generarDigitos(CBU_LENGTH);
            if (cuentaRepository.findByCbu(cbu).isEmpty()) {
                flag = true;
            }
        }
        return cbu;
    }
}
