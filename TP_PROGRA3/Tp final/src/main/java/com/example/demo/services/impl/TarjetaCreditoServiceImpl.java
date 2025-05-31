    package com.example.demo.services.impl;

    import com.example.demo.dto.TarjetaCreditoEntradaDTO;
    import com.example.demo.dto.TarjetaCreditoSalidaDTO;
    import com.example.demo.entities.Cuenta;
    import com.example.demo.entities.TarjetaCredito;
    import com.example.demo.repositories.CuentaRepository;
    import com.example.demo.repositories.TarjetaCreditoRepository;
    import com.example.demo.services.TarjetaCreditoService;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.stereotype.Service;

    import java.util.List;
    import java.util.Optional;
    import java.util.stream.Collectors;

    @Service
    public class TarjetaCreditoServiceImpl implements TarjetaCreditoService {

        @Autowired
        private TarjetaCreditoRepository repository;

        @Autowired
        private CuentaRepository cuentaRepository;

        private TarjetaCreditoSalidaDTO mapToSalidaDTO(TarjetaCredito tarjeta) {
            return TarjetaCreditoSalidaDTO.builder()
                    .tarjetaId(tarjeta.getTarjetaId())
                    .numero(tarjeta.getNumero())
                    .vencimiento(tarjeta.getVencimiento())
                    .bloqueada(tarjeta.isBloqueada())
                    .marca(tarjeta.getMarca())
                    .limite(tarjeta.getLimite())
                    .cuentaId(tarjeta.getCuenta().getCuentaId())
                    .build();
        }

        @Override
        public TarjetaCreditoSalidaDTO crear(TarjetaCreditoEntradaDTO dto) {
            Cuenta cuenta = cuentaRepository.findById(dto.getCuentaId()).orElseThrow();
            TarjetaCredito tarjeta = TarjetaCredito.builder()
                    .numero(dto.getNumero())
                    .vencimiento(dto.getVencimiento())
                    .codigoSeguridad(dto.getCodigoSeguridad())
                    .bloqueada(false)
                    .marca(dto.getMarca())
                    .limite(dto.getLimite())
                    .cuenta(cuenta)
                    .build();
            return mapToSalidaDTO(repository.save(tarjeta));
        }

        @Override
        public Optional<TarjetaCreditoSalidaDTO> buscarPorId(Long id) {
            return repository.findById(id).map(this::mapToSalidaDTO);
        }

        @Override
        public Optional<TarjetaCreditoSalidaDTO> findByNumero(String numero) {
            return repository.findByNumero(numero).map(this::mapToSalidaDTO);
        }

        @Override
        public List<TarjetaCreditoSalidaDTO> listarTodas() {
            return repository.findAll().stream()
                    .map(this::mapToSalidaDTO)
                    .collect(Collectors.toList());
        }

        @Override
        public TarjetaCreditoSalidaDTO actualizar(Long id, TarjetaCreditoEntradaDTO dto) {
            TarjetaCredito tarjeta = repository.findById(id).orElseThrow();
            tarjeta.setMarca(dto.getMarca());
            tarjeta.setVencimiento(dto.getVencimiento());
            tarjeta.setLimite(dto.getLimite());
            return mapToSalidaDTO(repository.save(tarjeta));
        }

        @Override
        public void eliminar(Long id) {
            repository.deleteById(id);
        }

        @Override
        public void pagarTarjeta(Long id, double monto) {
            // lógica de pago de tarjeta
        }

        @Override
        public boolean pagarConTarjeta(Long id, double monto) {
            // lógica de consumo con tarjeta
            return true;
        }
    }