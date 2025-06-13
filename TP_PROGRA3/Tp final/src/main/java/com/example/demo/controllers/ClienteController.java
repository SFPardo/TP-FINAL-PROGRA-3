package com.example.demo.controllers;
import com.example.demo.dto.ClienteSalidaDTO;
import com.example.demo.dto.DomicilioEntradaSalidaDTO;
import com.example.demo.dto.ClienteEntradaDTO;
import com.example.demo.services.impl.ClienteServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    @Autowired
    private ClienteServiceImpl clienteServiceImpl;

    // -- METODOS POST -- //
    @Operation(summary = "Crear un nuevo cliente desde un usuario ADMIN",
            description = "Crea un nuevo cliente con usuario y cuenta. Accesible solo por ADMIN.Permite otorgar permisos de administrador al cliente.")
    @PostMapping("/admin/crear")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> crearClienteAdmin(@Valid @RequestBody ClienteEntradaDTO dto) {
        ClienteSalidaDTO cliente = clienteServiceImpl.crearClienteAdmin(dto);
        return ResponseEntity.ok(cliente);
    }

    @Operation(summary = "Crear múltiples clientes desde un usuario ADMIN",
            description = "Permite a un usuario ADMIN crear una lista de nuevos clientes, cada uno con su usuario y cuenta. Se puede especificar el rol (ADMIN/CLIENTE) para cada cliente.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Lista de clientes a crear (cada uno con su usuario, credenciales y rol).",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ClienteEntradaDTO[].class),
                            examples = @ExampleObject(name = "Ejemplo de creación de múltiples clientes",
                                    value = "[" +
                                            "{\"nombre\":\"Ana\",\"apellido\":\"García\",\"dni\":\"35000111\",\"fechaNacimiento\":\"1988-01-01\",\"telefono\":\"1122223333\",\"email\":\"ana.garcia@example.com\",\"domicilio\":{\"provincia\":\"Bs As\",\"ciudad\":\"La Plata\",\"calle\":\"Calle 1\",\"altura\":100},\"usuario\":{\"nombreUsuario\":\"ana.g\",\"credenciales\":{\"pin\":\"0000\"},\"tipoRol\":\"CLIENTE\"}}," +
                                            "{\"nombre\":\"Luis\",\"apellido\":\"Martínez\",\"dni\":\"36000222\",\"fechaNacimiento\":\"1992-02-02\",\"telefono\":\"1144445555\",\"email\":\"luis.m@example.com\",\"domicilio\":{\"provincia\":\"Córdoba\",\"ciudad\":\"Córdoba\",\"calle\":\"Av. Central\",\"altura\":200},\"usuario\":{\"nombreUsuario\":\"luis.m\",\"credenciales\":{\"pin\":\"1111\"},\"tipoRol\":\"ADMIN\"}}" +
                                            "]")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Clientes creados exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida o errores de validación en alguno de los clientes."),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere el rol ADMIN.")
            })
    @PostMapping("/admin/crearMuchos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClienteSalidaDTO>> crearMultiplesClientesAdmin(
            @Valid @RequestBody List<ClienteEntradaDTO> dtos) { // Recibe una LISTA de DTOs
        List<ClienteSalidaDTO> clientesCreados = clienteServiceImpl.crearMultiplesClientesAdmin(dtos);
        return ResponseEntity.ok(clientesCreados);
    }

    @Operation(summary = "Crear un nuevo cliente",
            description = "Crea un nuevo cliente con usuario y cuenta. Accesible por cualquier usuario.Solo otorga permisos de cliente al nuevo cliente.")
    @PostMapping("/crear")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ClienteSalidaDTO> crearClienteSinPermisos(@Valid @RequestBody ClienteEntradaDTO dto) {
        ClienteSalidaDTO cliente = clienteServiceImpl.crearClienteSinPermisos(dto);
        return ResponseEntity.ok(cliente);
    }

    // -- METODOS GET -- //
    @Operation(summary = "Obtener todos los clientes paginados",
            description = "Solo accesible por usuarios con rol ADMIN. Devuelve una lista paginada de todos los clientes.",
            parameters = {
                    @Parameter(name = "page", description = "Número de página (0-indexed).", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
                    @Parameter(name = "size", description = "Número de elementos por página.", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
                    @Parameter(name = "sort", description = "Criterio de ordenamiento (ej. nombre,asc o id,desc).", in = ParameterIn.QUERY, schema = @Schema(type = "string", example = "nombre,asc"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista paginada de clientes obtenida exitosamente.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class, subTypes = {ClienteSalidaDTO.class}))),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere el rol ADMIN.")
            })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ClienteSalidaDTO>> listarClientes(
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(clienteServiceImpl.obtenerTodosLosClientesPaginados(pageable));
    }

    @Operation(summary = "Buscar cliente por ID",
            description = "Accesible por ADMIN (cualquier ID).")
    @GetMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> buscarPorId(@PathVariable Long id) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.buscarClientePorIdConDTO(id);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Buscar cliente por DNI",
            description = "Accesible por ADMIN (cualquier DNI)")
    @GetMapping("/dni/{dni}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> buscarPorDni(@PathVariable String dni) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.mapToSalidaDTO(clienteServiceImpl.buscarClientePorDni(dni));
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Buscar cliente por email",
            description = "Accesible por ADMIN (cualquier email)")
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> buscarPorEmail(@PathVariable String email) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.mapToSalidaDTO(clienteServiceImpl.buscarClientePorEmail(email));
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Buscar cliente por telefono",
            description = "Accesible por ADMIN (cualquier telefono)")
    @GetMapping("/telefono/{telefono}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> buscarPorTelefono(@PathVariable String telefono) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.mapToSalidaDTO(clienteServiceImpl.buscarClientePorTelefono(telefono));
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Ver el domicilio del cliente autenticado",
            description = "Devuelve los detalles del domicilio del cliente actualmente autenticado. No requiere ID en la URL.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Domicilio encontrado exitosamente."),
                    @ApiResponse(responseCode = "404", description = "Domicilio no encontrado para el cliente autenticado (puede que el usuario no tenga cliente asociado o domicilio)."),
                    @ApiResponse(responseCode = "401", description = "No autorizado (falta token JWT o es inválido).")
            })
    @GetMapping("/mi-domicilio")
    @PreAuthorize("isAuthenticated()") // Requiere que el usuario esté autenticado
    public ResponseEntity<DomicilioEntradaSalidaDTO> verMiDomicilio(Authentication authentication) {
        String nombreUsuario = authentication.getName();

        DomicilioEntradaSalidaDTO domicilio = clienteServiceImpl.verMiDomicilio(nombreUsuario);

        return domicilio != null ? ResponseEntity.ok(domicilio) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Buscar cliente por Alias de cuenta",
            description = "Accesible por ADMIN (cualquier alias).")
    @GetMapping("/alias/{alias}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> obtenerPorAlias(@PathVariable String alias) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.buscarClientePorAlias(alias);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @Operation(summary = "Buscar cliente por CBU",
            description = "Accesible por ADMIN (cualquier CBU).")
    @GetMapping("/cbu/{cbu}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> obtenerPorCbu(@PathVariable String cbu) {
        try {
            ClienteSalidaDTO dto = clienteServiceImpl.buscarClientePorCbu(cbu);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar clientes por provincia",
            description = "Accesible por ADMIN (cualquier provincia).")
    @GetMapping("/provincia/{provincia}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClienteSalidaDTO>> obtenerPorProvincia(@PathVariable String provincia) {
        try {
            List<ClienteSalidaDTO> clientes = clienteServiceImpl.buscarClientesPorProvincia(provincia);
            return ResponseEntity.ok(clientes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Buscar clientes por ciudad",
            description = "Accesible por ADMIN (cualquier ciudad).")
    @GetMapping("/ciudad/{ciudad}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ClienteSalidaDTO>> obtenerPorCiudad(@PathVariable String ciudad) {
        try {
            List<ClienteSalidaDTO> clientes = clienteServiceImpl.buscarClientesPorCiudad(ciudad);
            return ResponseEntity.ok(clientes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // -- METODOS DELETE -- //

    @Operation(summary = "Eliminar cliente por ID",
            description = "Accesible por ADMIN (cualquier ID). Elimina un cliente por su ID.")
    @DeleteMapping("/id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarClientePorId(@PathVariable Long id) {
        clienteServiceImpl.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar cliente por DNI",
            description = "Accesible por ADMIN (cualquier DNI). Elimina un cliente por su DNI.")
    @DeleteMapping("/dni/{dni}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarClientePorDni(@PathVariable String dni) {
        clienteServiceImpl.eliminarClientePorDni(dni);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Eliminar cliente por email",
            description = "Accesible por ADMIN (cualquier email). Elimina un cliente por su email.")
    @DeleteMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarClientePorEmail(@PathVariable String email) {
        clienteServiceImpl.eliminarClientePorEmail(email);
        return ResponseEntity.noContent().build();
    }


    // -- METODOS PUT y PATCH -- //

    @Operation(summary = "Actualizar cliente por ID",
            description = "Accesible por ADMIN (cualquier ID). Actualiza los datos de un cliente.")
    @PutMapping("/{id}/domicilio")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> actualizarDomicilio(
            @PathVariable Long id,
            @RequestBody DomicilioEntradaSalidaDTO nuevoDomicilio) {
        return ResponseEntity.ok(clienteServiceImpl.actualizarDomicilio(id, nuevoDomicilio));
    }

    @Operation(summary = "Actualizar el domicilio del cliente autenticado",
            description = "Permite al cliente autenticado actualizar su propio domicilio. No requiere ID en la URL. El usuario autenticado debe tener el rol CLIENTE o ADMIN.")
    @PutMapping("/mi-domicilio")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<DomicilioEntradaSalidaDTO> actualizarMiDomicilio(
            Authentication authentication,
            @Valid @RequestBody DomicilioEntradaSalidaDTO dto) {

        String nombreUsuario = authentication.getName();
        DomicilioEntradaSalidaDTO domicilioActualizado = clienteServiceImpl.actualizarDomicilioClienteAutenticado(nombreUsuario, dto);

        return domicilioActualizado != null ? ResponseEntity.ok(domicilioActualizado) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Actualizar el nombre de un cliente por ID (Solo ADMIN)",
            description = "Permite a un ADMIN actualizar el nombre de un cliente específico por su ID. El nuevo nombre se envía directamente en el cuerpo de la solicitud como texto plano.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo nombre para el cliente (en formato de texto plano)",
                    required = true,
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string"),
                            examples = @ExampleObject(name = "Ejemplo de cambio de nombre", value = "NuevoNombreCliente")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nombre del cliente actualizado exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida (cuerpo vacío o nulo)."),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere el rol ADMIN."),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado con el ID especificado.")
            })
    @PatchMapping("/{id}/nombre")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> actualizarNombre(
            @PathVariable Long id,
            @RequestBody String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        ClienteSalidaDTO clienteActualizado = clienteServiceImpl.cambiarNombre(id, nuevoNombre.trim());
        return ResponseEntity.ok(clienteActualizado);
    }

    @Operation(summary = "Actualizar email de un cliente por ID (Solo ADMIN)",
            description = "Permite a un ADMIN actualizar el email de un cliente específico por su ID. El nuevo email se envía directamente en el cuerpo de la solicitud como texto plano. Incluye validación de formato de email.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo email para el cliente (en formato de texto plano)",
                    required = true,
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string", format = "email"),
                            examples = @ExampleObject(name = "Ejemplo de cambio de email", value = "nuevo.email@example.com")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email del cliente actualizado exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida (cuerpo vacío o nulo, o formato de email incorrecto)."),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere el rol ADMIN."),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado con el ID especificado.")
            })
    @PatchMapping("/{id}/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> actualizarEmail(
            @PathVariable Long id,
            @RequestBody String nuevoEmail) {

        if (nuevoEmail == null || nuevoEmail.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (!nuevoEmail.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) {
            return ResponseEntity.badRequest().body(null);
        }

        ClienteSalidaDTO clienteActualizado = clienteServiceImpl.cambiarEmail(id, nuevoEmail.trim());
        return ResponseEntity.ok(clienteActualizado);
    }

    @Operation(summary = "Actualizar teléfono de un cliente por ID (Solo ADMIN)",
            description = "Permite a un ADMIN actualizar el teléfono de un cliente específico por su ID. El nuevo teléfono se envía directamente en el cuerpo de la solicitud como texto plano. Incluye validación de formato de teléfono (10 dígitos).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nuevo teléfono para el cliente (en formato de texto plano)",
                    required = true,
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string"),
                            examples = @ExampleObject(name = "Ejemplo de cambio de teléfono", value = "1198765432")
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Teléfono del cliente actualizado exitosamente."),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida (cuerpo vacío o nulo, o formato de teléfono incorrecto)."),
                    @ApiResponse(responseCode = "403", description = "Acceso denegado. Se requiere el rol ADMIN."),
                    @ApiResponse(responseCode = "404", description = "Cliente no encontrado con el ID especificado.")
            })
    @PatchMapping("/{id}/telefono")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteSalidaDTO> actualizarTelefono(
            @PathVariable Long id,
            @RequestBody String nuevoTelefono) {
        if (nuevoTelefono == null || nuevoTelefono.trim().isEmpty() || !nuevoTelefono.matches("\\d{10}")) {
            return ResponseEntity.badRequest().body(null);
        }
        ClienteSalidaDTO clienteActualizado = clienteServiceImpl.cambiarTelefono(id, nuevoTelefono.trim());
        return ResponseEntity.ok(clienteActualizado);
    }





}
