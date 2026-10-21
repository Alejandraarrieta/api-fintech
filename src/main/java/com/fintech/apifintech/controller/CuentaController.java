package com.fintech.apifintech.controller;

import com.fintech.apifintech.dto.CuentaRequest;
import com.fintech.apifintech.dto.CuentaResponse;
import com.fintech.apifintech.model.Cuenta;
import com.fintech.apifintech.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cuentas")
@Tag(name = "Cuentas", description = "Operaciones para administrar cuentas fintech")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) { this.cuentaService = cuentaService; }

    @GetMapping
    @Operation(summary = "Listar cuentas")
    public List<CuentaResponse> listar() {
        return cuentaService.listar().stream().map(CuentaResponse::desde).toList();
    }

    @PostMapping
    @Operation(summary = "Crear una cuenta asociada a un usuario existente")
    public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CuentaRequest request) {
        Cuenta creada = cuentaService.crear(request);
        return ResponseEntity.created(URI.create("/cuentas/" + creada.getId()))
                .body(CuentaResponse.desde(creada));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar una cuenta por identificador")
    public CuentaResponse obtener(@PathVariable Long id) {
        return CuentaResponse.desde(cuentaService.obtener(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Reemplazar los datos de una cuenta")
    public CuentaResponse actualizar(@PathVariable Long id, @Valid @RequestBody CuentaRequest request) {
        return CuentaResponse.desde(cuentaService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una cuenta")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cuentaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
