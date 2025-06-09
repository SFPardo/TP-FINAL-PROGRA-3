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
    import org.springframework.transaction.annotation.Transactional;


    import java.math.BigDecimal;
    import java.time.LocalDate;
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
        @Transactional
        public TarjetaCreditoSalidaDTO crear(TarjetaCreditoEntradaDTO dto) {
            Cuenta cuenta = cuentaRepository.findById(dto.getCuentaId()).orElseThrow();
            TarjetaCredito tarjeta = TarjetaCredito.builder()
                    .numero(dto.getNumero())
                    .vencimiento(dto.getVencimiento())
                    .codigoSeguridad(dto.getCodigoSeguridad())
                    .bloqueada(false)
                    .marca(dto.getMarca())
                    .limite(dto.getLimite())
                    .saldo(BigDecimal.ZERO)
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
        @Transactional
        public TarjetaCreditoSalidaDTO actualizar(Long id, TarjetaCreditoEntradaDTO dto) {
            TarjetaCredito tarjeta = repository.findById(id).orElseThrow();
            tarjeta.setMarca(dto.getMarca());
            tarjeta.setVencimiento(dto.getVencimiento());
            tarjeta.setLimite(dto.getLimite());
            
            return mapToSalidaDTO(repository.save(tarjeta));
        }

        @Override
        @Transactional
        public void eliminar(Long id) {
            repository.deleteById(id);
        }

        @Override
        @Transactional
        public void pagarTarjeta(Long id, double monto) {
            TarjetaCredito tarjeta = repository.findById(id)
                    .filter(t -> t instanceof TarjetaCredito)
                    .map(t -> (TarjetaCredito) t)
                    .orElseThrow();

            if (tarjeta.isBloqueada() || tarjeta.getVencimiento().isBefore(LocalDate.now())) {
                throw new IllegalStateException("Tarjeta bloqueada o vencida");
            }

            BigDecimal montoPago = BigDecimal.valueOf(monto);
            BigDecimal saldoActual = tarjeta.getSaldo();

            // Si el pago es mayor al saldo, solo se paga lo que se debe
            if (montoPago.compareTo(saldoActual) > 0) {
                montoPago = saldoActual;
            }

            tarjeta.setSaldo(saldoActual.subtract(montoPago));
            repository.save(tarjeta);
        }

        @Override
        @Transactional
        public boolean pagarConTarjeta(Long id, double monto) {
            TarjetaCredito tarjeta = repository.findById(id)
                    .filter(t -> t instanceof TarjetaCredito)
                    .map(t -> (TarjetaCredito) t)
                    .orElseThrow();

            if (tarjeta.isBloqueada() || tarjeta.getVencimiento().isBefore(LocalDate.now())) {
                return false;
            }

            BigDecimal montoCompra = BigDecimal.valueOf(monto);
            BigDecimal disponible = tarjeta.getLimite().subtract(tarjeta.getSaldo());

            if (disponible.compareTo(montoCompra) < 0) {
                return false; // No hay suficiente crédito
            }

            tarjeta.setSaldo(tarjeta.getSaldo().add(montoCompra));
            repository.save(tarjeta);
            return true;
        }
    }