package com.fintech.apifintech.dto;

import com.fintech.apifintech.model.TipoCuenta;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record CuentaRequest(
        @NotBlank @Pattern(regexp = "^[A-Za-z0-9-]{6,22}$") String numero,
        @NotNull TipoCuenta tipo,
        @NotNull @DecimalMin(value = "0.00") BigDecimal saldo,
        @NotNull @Positive Long usuarioId) { }
