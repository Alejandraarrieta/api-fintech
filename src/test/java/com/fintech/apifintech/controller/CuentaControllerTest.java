package com.fintech.apifintech.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fintech.apifintech.exception.ConflictoException;
import com.fintech.apifintech.exception.RecursoNoEncontradoException;
import com.fintech.apifintech.model.Cuenta;
import com.fintech.apifintech.model.TipoCuenta;
import com.fintech.apifintech.model.Usuario;
import com.fintech.apifintech.service.CuentaService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean CuentaService cuentaService;

    @Test
    void listaCuentasYDevuelveDatosPublicos() throws Exception {
        Cuenta cuenta = cuenta(7L, "AR-123456", new BigDecimal("100.00"));
        when(cuentaService.listar()).thenReturn(List.of(cuenta));

        mockMvc.perform(get("/cuentas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numero").value("AR-123456"))
                .andExpect(jsonPath("$[0].usuarioId").value(1));
    }

    @Test
    void altaValidaDevuelve201YLocation() throws Exception {
        Cuenta creada = cuenta(12L, "AR-123456", new BigDecimal("100.00"));
        when(cuentaService.crear(any())).thenReturn(creada);

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("AR-123456", 100, 1)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/cuentas/12"))
                .andExpect(jsonPath("$.numero").value("AR-123456"));
    }

    @Test
    void consultaPorIdDevuelveLaCuenta() throws Exception {
        Cuenta cuenta = cuenta(7L, "AR-123456", new BigDecimal("100.00"));
        when(cuentaService.obtener(7L)).thenReturn(cuenta);

        mockMvc.perform(get("/cuentas/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.numero").value("AR-123456"));
    }

    @Test
    void actualizacionValidaDevuelveCuentaActualizada() throws Exception {
        Cuenta cuentaActualizada = cuenta(7L, "AR-123456", new BigDecimal("27500.00"));
        when(cuentaService.actualizar(org.mockito.ArgumentMatchers.eq(7L), any()))
                .thenReturn(cuentaActualizada);

        mockMvc.perform(put("/cuentas/7")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("AR-123456", 27500, 1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.saldo").value(27500.00));
    }

    @Test
    void eliminacionValidaDevuelve204() throws Exception {
        mockMvc.perform(delete("/cuentas/7"))
                .andExpect(status().isNoContent());
    }

    @Test
    void entradaInvalidaDevuelve400ConDetalleYNoInvocaServicio() throws Exception {
        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("", -1, 0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value("/cuentas"))
                .andExpect(jsonPath("$.details.numero").exists())
                .andExpect(jsonPath("$.details.saldo").exists())
                .andExpect(jsonPath("$.details.usuarioId").exists());

        verifyNoInteractions(cuentaService);
    }

    @Test
    void cuentaInexistenteDevuelve404Estructurado() throws Exception {
        when(cuentaService.obtener(404L))
                .thenThrow(new RecursoNoEncontradoException("No existe la cuenta 404"));

        mockMvc.perform(get("/cuentas/404"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("No existe la cuenta 404"))
                .andExpect(jsonPath("$.path").value("/cuentas/404"));
    }

    @Test
    void conflictoDeDominioDevuelve409() throws Exception {
        when(cuentaService.crear(any()))
                .thenThrow(new ConflictoException("Ya existe una cuenta con el número indicado"));

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson("AR-123456", 100, 1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Ya existe una cuenta con el número indicado"));
    }

    private static String requestJson(String numero, int saldo, int usuarioId) {
        return "{\"numero\":\"" + numero + "\",\"tipo\":\"CAJA_AHORRO\",\"saldo\":"
                + saldo + ",\"usuarioId\":" + usuarioId + "}";
    }

    private static Cuenta cuenta(Long id, String numero, BigDecimal saldo) {
        Cuenta cuenta = mock(Cuenta.class);
        Usuario usuario = mock(Usuario.class);
        when(cuenta.getId()).thenReturn(id);
        when(cuenta.getNumero()).thenReturn(numero);
        when(cuenta.getTipo()).thenReturn(TipoCuenta.CAJA_AHORRO);
        when(cuenta.getSaldo()).thenReturn(saldo);
        when(cuenta.getUsuario()).thenReturn(usuario);
        when(usuario.getId()).thenReturn(1L);
        when(usuario.getNombre()).thenReturn("Ana Pérez");
        when(usuario.getEmail()).thenReturn("ana@example.com");
        return cuenta;
    }
}
