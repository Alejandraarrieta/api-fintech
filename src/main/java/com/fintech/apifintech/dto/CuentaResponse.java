package com.fintech.apifintech.dto;

import com.fintech.apifintech.model.Cuenta;
import com.fintech.apifintech.model.TipoCuenta;
import java.math.BigDecimal;

public record CuentaResponse(Long id, String numero, TipoCuenta tipo, BigDecimal saldo,
                             Long usuarioId, String usuarioNombre, String usuarioEmail) {
    public static CuentaResponse desde(Cuenta cuenta) {
        return new CuentaResponse(cuenta.getId(), cuenta.getNumero(), cuenta.getTipo(),
                cuenta.getSaldo(), cuenta.getUsuario().getId(), cuenta.getUsuario().getNombre(),
                cuenta.getUsuario().getEmail());
    }
}
